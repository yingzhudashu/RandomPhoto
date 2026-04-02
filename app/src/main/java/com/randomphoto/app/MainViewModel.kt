package com.randomphoto.app

import android.app.Application
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.IntentSender
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.randomphoto.app.data.Photo
import com.randomphoto.app.data.PhotoRepository
import com.randomphoto.app.data.PhotoSyncManager
import com.randomphoto.app.data.RecycleBinManager
import com.randomphoto.app.data.RecycleBinPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState {
    object Loading : UiState()
    object PermissionRequired : UiState()
    data class Success(val photos: List<Photo>, val currentPhoto: Photo?) : UiState()
    data class Error(val message: String) : UiState()
    object NoPhotos : UiState()
}

data class RecycleBinUiState(
    val photos: List<RecycleBinPhoto> = emptyList(),
    val isLoading: Boolean = false,
    val totalSize: Long = 0,
    val message: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val photoRepository = PhotoRepository(application)
    val recycleBinManager = RecycleBinManager(application)
    private val syncManager = PhotoSyncManager(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _recycleBinState = MutableStateFlow(RecycleBinUiState())
    val recycleBinState: StateFlow<RecycleBinUiState> = _recycleBinState.asStateFlow()

    private var allPhotos: List<Photo> = emptyList()
    private var currentPhoto: Photo? = null
    private var isLoaded = false
    private var lastKnownSyncVersion = 0

    // 当前正在删除的照片信息（两步流程用）
    private var pendingDeletePhoto: Photo? = null
    private var pendingRecycleBinId: String? = null

    fun loadPhotos() {
        if (isLoaded) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val photos = photoRepository.loadAllPhotos()
                if (photos.isEmpty()) {
                    _uiState.value = UiState.NoPhotos
                } else {
                    allPhotos = photos
                    isLoaded = true
                    if (pendingSyncUri != null) {
                        checkPendingSync()
                    } else {
                        currentPhoto = photos.randomOrNull()
                        _uiState.value = UiState.Success(photos, currentPhoto)
                        currentPhoto?.let { syncCurrentPhotoToPrefs(it) }
                    }
                }
                lastKnownSyncVersion = syncManager.getSyncVersion()
                recycleBinManager.autoCleanExpired()
            } catch (e: Exception) {
                _uiState.value = UiState.Error("加载失败: ${e.message}")
            }
        }
    }

    fun nextRandomPhoto() {
        if (allPhotos.isEmpty()) return
        viewModelScope.launch {
            currentPhoto = allPhotos.randomOrNull()
            val currentState = _uiState.value
            if (currentState is UiState.Success) {
                _uiState.value = UiState.Success(currentState.photos, currentPhoto)
            }
            currentPhoto?.let { syncCurrentPhotoToPrefs(it) }
        }
    }

    private fun syncCurrentPhotoToPrefs(photo: Photo) {
        val mediaId = ContentUris.parseId(photo.uri)
        syncManager.setAppCurrentPhoto(mediaId)
    }

    private var pendingSyncUri: android.net.Uri? = null

    fun showSpecificPhoto(uri: android.net.Uri) {
        pendingSyncUri = uri
        viewModelScope.launch {
            if (!isLoaded) {
                val photos = photoRepository.loadAllPhotos()
                if (photos.isEmpty()) return@launch
                allPhotos = photos
                isLoaded = true
            }
            doSyncPhoto(uri)
        }
    }

    private fun doSyncPhoto(uri: android.net.Uri) {
        val targetPhoto = allPhotos.find { it.uri == uri }
        if (targetPhoto != null) {
            currentPhoto = targetPhoto
            _uiState.value = UiState.Success(allPhotos, currentPhoto)
            pendingSyncUri = null
            return
        }
        val mediaId = ContentUris.parseId(uri)
        val matchById = allPhotos.find { ContentUris.parseId(it.uri) == mediaId }
        if (matchById != null) {
            currentPhoto = matchById
            _uiState.value = UiState.Success(allPhotos, currentPhoto)
            pendingSyncUri = null
        }
    }

    private fun checkPendingSync() {
        pendingSyncUri?.let { doSyncPhoto(it) }
    }

    fun checkSyncState() {
        viewModelScope.launch {
            val currentVersion = syncManager.getSyncVersion()
            if (currentVersion != lastKnownSyncVersion) {
                lastKnownSyncVersion = currentVersion
                refreshPhotoList()
            }
            currentPhoto?.let { photo ->
                val mediaId = ContentUris.parseId(photo.uri)
                if (!syncManager.isPhotoStillExists(mediaId)) {
                    allPhotos = allPhotos.filter { it.uri != photo.uri }
                    if (allPhotos.isEmpty()) {
                        _uiState.value = UiState.NoPhotos
                    } else {
                        currentPhoto = allPhotos.randomOrNull()
                        _uiState.value = UiState.Success(allPhotos, currentPhoto)
                        currentPhoto?.let { syncCurrentPhotoToPrefs(it) }
                    }
                }
            }
        }
    }

    private suspend fun refreshPhotoList() {
        try {
            val photos = photoRepository.loadAllPhotos()
            allPhotos = photos
            if (photos.isEmpty()) {
                _uiState.value = UiState.NoPhotos
                currentPhoto = null
            } else {
                val currentStillExists = currentPhoto?.let { cp ->
                    photos.any { it.uri == cp.uri }
                } ?: false
                if (!currentStillExists) currentPhoto = photos.randomOrNull()
                _uiState.value = UiState.Success(photos, currentPhoto)
                currentPhoto?.let { syncCurrentPhotoToPrefs(it) }
            }
        } catch (_: Exception) { }
    }

    // ========== 删除流程（两步：复制 → 系统确认删除）==========

    sealed class DeleteRequestResult {
        /** 直接成功（旧版本 Android 或 app 自己创建的文件） */
        object Success : DeleteRequestResult()
        /** 需要弹系统确认框 */
        data class NeedsConfirmation(val intentSender: IntentSender) : DeleteRequestResult()
        data class Failure(val error: String) : DeleteRequestResult()
    }

    /**
     * 删除当前照片：
     * 1. 先复制到回收站
     * 2. 尝试从 MediaStore 删除（可能需要系统确认）
     */
    suspend fun deleteCurrentPhoto(): DeleteRequestResult {
        val photoToDelete = currentPhoto ?: return DeleteRequestResult.Failure("没有可删除的照片")

        // 第一步：复制到回收站
        val recycleBinId = recycleBinManager.copyToRecycleBin(photoToDelete)
            ?: return DeleteRequestResult.Failure("复制到回收站失败")

        pendingDeletePhoto = photoToDelete
        pendingRecycleBinId = recycleBinId

        // 第二步：尝试从 MediaStore 删除
        return withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Android 11+：createDeleteRequest
                    val deleteRequest = MediaStore.createDeleteRequest(
                        getApplication<Application>().contentResolver,
                        listOf(photoToDelete.uri)
                    )
                    DeleteRequestResult.NeedsConfirmation(deleteRequest.intentSender)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10：尝试直接删除，捕获 RecoverableSecurityException
                    try {
                        val rows = getApplication<Application>().contentResolver.delete(photoToDelete.uri, null, null)
                        if (rows > 0) {
                            onDeleteConfirmed()
                            DeleteRequestResult.Success
                        } else {
                            // 尝试 createDeleteRequest
                            val deleteRequest = MediaStore.createDeleteRequest(
                                getApplication<Application>().contentResolver,
                                listOf(photoToDelete.uri)
                            )
                            DeleteRequestResult.NeedsConfirmation(deleteRequest.intentSender)
                        }
                    } catch (e: RecoverableSecurityException) {
                        DeleteRequestResult.NeedsConfirmation(e.userAction.actionIntent.intentSender)
                    }
                } else {
                    // Android 9 及以下：直接删除
                    val rows = getApplication<Application>().contentResolver.delete(photoToDelete.uri, null, null)
                    if (rows > 0) {
                        onDeleteConfirmed()
                        DeleteRequestResult.Success
                    } else {
                        recycleBinManager.rollbackCopy(recycleBinId)
                        pendingDeletePhoto = null
                        pendingRecycleBinId = null
                        DeleteRequestResult.Failure("删除失败")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "deleteCurrentPhoto error", e)
                recycleBinManager.rollbackCopy(recycleBinId)
                pendingDeletePhoto = null
                pendingRecycleBinId = null
                DeleteRequestResult.Failure("删除异常: ${e.message}")
            }
        }
    }

    /**
     * 系统确认删除成功后调用
     */
    fun completeDelete() {
        onDeleteConfirmed()
    }

    private fun onDeleteConfirmed() {
        val photo = pendingDeletePhoto
        val rbId = pendingRecycleBinId

        if (rbId != null) {
            recycleBinManager.confirmMove(rbId)
        }

        if (photo != null) {
            val mediaId = ContentUris.parseId(photo.uri)
            syncManager.notifyPhotoDeleted(mediaId)
            allPhotos = allPhotos.filter { it.uri != photo.uri }
        }

        currentPhoto = null
        pendingDeletePhoto = null
        pendingRecycleBinId = null

        if (allPhotos.isEmpty()) {
            _uiState.value = UiState.NoPhotos
        } else {
            _uiState.value = UiState.Success(allPhotos, null)
            nextRandomPhoto()
        }
    }

    /**
     * 用户取消了系统删除确认
     */
    fun cancelDelete() {
        pendingRecycleBinId?.let { recycleBinManager.rollbackCopy(it) }
        pendingDeletePhoto = null
        pendingRecycleBinId = null
    }

    // ========== 回收站操作 ==========

    fun loadRecycleBin() {
        viewModelScope.launch {
            _recycleBinState.value = _recycleBinState.value.copy(isLoading = true)
            val photos = recycleBinManager.listRecycleBinPhotos()
            val totalSize = recycleBinManager.getTotalSize()
            _recycleBinState.value = RecycleBinUiState(
                photos = photos, isLoading = false, totalSize = totalSize
            )
        }
    }

    fun restoreFromRecycleBin(photo: RecycleBinPhoto) {
        viewModelScope.launch {
            val success = recycleBinManager.restorePhoto(photo)
            if (success) {
                syncManager.notifyPhotoRestored()
                loadRecycleBin()
                isLoaded = false
                loadPhotos()
                _recycleBinState.value = _recycleBinState.value.copy(message = "已恢复: ${photo.originalName}")
            } else {
                _recycleBinState.value = _recycleBinState.value.copy(message = "恢复失败")
            }
        }
    }

    fun permanentDeleteFromRecycleBin(photo: RecycleBinPhoto) {
        viewModelScope.launch {
            val success = recycleBinManager.permanentDelete(photo)
            if (success) {
                loadRecycleBin()
                _recycleBinState.value = _recycleBinState.value.copy(message = "已永久删除")
            } else {
                _recycleBinState.value = _recycleBinState.value.copy(message = "删除失败")
            }
        }
    }

    fun clearRecycleBin() {
        viewModelScope.launch {
            val success = recycleBinManager.clearAll()
            if (success) {
                loadRecycleBin()
                _recycleBinState.value = _recycleBinState.value.copy(message = "回收站已清空")
            }
        }
    }

    fun clearRecycleBinMessage() {
        _recycleBinState.value = _recycleBinState.value.copy(message = null)
    }

    fun onPermissionDenied() { _uiState.value = UiState.PermissionRequired }
    fun getCurrentPhoto(): Photo? = currentPhoto
    fun refreshAfterPermissionGranted() { isLoaded = false; loadPhotos() }
}
