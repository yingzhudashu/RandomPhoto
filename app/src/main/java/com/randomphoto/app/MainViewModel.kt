package com.randomphoto.app

import android.app.Application
import android.content.IntentSender
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.randomphoto.app.data.Photo
import com.randomphoto.app.data.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI 状态模型
 */
sealed class UiState {
    object Loading : UiState()
    object PermissionRequired : UiState()
    data class Success(val photos: List<Photo>, val currentPhoto: Photo?) : UiState()
    data class Error(val message: String) : UiState()
    object NoPhotos : UiState()
}

/**
 * 主 ViewModel - 管理照片的加载、随机选择和删除
 * 使用 StateFlow 暴露 UI 状态
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val photoRepository = PhotoRepository(application)
    
    // UI 状态
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // 所有照片列表
    private var allPhotos: List<Photo> = emptyList()
    
    // 当前显示的照片
    private var currentPhoto: Photo? = null
    
    // 是否已加载照片
    private var isLoaded = false
    
    /**
     * 加载照片列表
     * 只在第一次调用时加载
     */
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
                    // 随机选择一张照片作为初始显示
                    currentPhoto = photos.randomOrNull()
                    _uiState.value = UiState.Success(photos, currentPhoto)
                }
                
                isLoaded = true
            } catch (e: Exception) {
                _uiState.value = UiState.Error("加载失败：${e.message}")
            }
        }
    }
    
    /**
     * 随机选择下一张照片
     */
    fun nextRandomPhoto() {
        if (allPhotos.isEmpty()) return
        
        viewModelScope.launch {
            // 从所有照片中随机选择一张（可能与当前相同，增加随机性）
            val newPhoto = allPhotos.randomOrNull()
            currentPhoto = newPhoto
            
            // 更新 UI 状态
            val currentState = _uiState.value
            if (currentState is UiState.Success) {
                _uiState.value = UiState.Success(currentState.photos, currentPhoto)
            }
        }
    }
    
    /**
     * 删除请求结果
     */
    sealed class DeleteRequestResult {
        object Success : DeleteRequestResult()
        data class NeedsConfirmation(val intentSender: IntentSender) : DeleteRequestResult()
        data class Failure(val error: String) : DeleteRequestResult()
    }
    
    /**
     * 删除当前照片（第一步：检查是否需要确认）
     * @return 删除请求结果
     */
    suspend fun deleteCurrentPhoto(): DeleteRequestResult {
        val photoToDelete = currentPhoto ?: return DeleteRequestResult.Failure("没有可删除的照片")
        
        return try {
            val result = photoRepository.deletePhoto(photoToDelete)
            
            when (result) {
                is PhotoRepository.DeleteResult.Success -> {
                    // 删除成功，更新列表
                    allPhotos = allPhotos.filter { it.uri != photoToDelete.uri }
                    currentPhoto = null
                    
                    // 更新 UI 状态
                    if (allPhotos.isEmpty()) {
                        _uiState.value = UiState.NoPhotos
                    } else {
                        _uiState.value = UiState.Success(allPhotos, null)
                        // 自动加载下一张
                        nextRandomPhoto()
                    }
                    DeleteRequestResult.Success
                }
                is PhotoRepository.DeleteResult.NeedsConfirmation -> {
                    // 需要用户确认
                    DeleteRequestResult.NeedsConfirmation(result.intentSender)
                }
                is PhotoRepository.DeleteResult.Failure -> {
                    _uiState.value = UiState.Error(result.error)
                    DeleteRequestResult.Failure(result.error)
                }
            }
        } catch (e: Exception) {
            _uiState.value = UiState.Error("删除异常：${e.message}")
            DeleteRequestResult.Failure("删除异常：${e.message}")
        }
    }
    
    /**
     * 完成删除（用户确认后的回调）
     */
    fun completeDelete() {
        // 从列表中移除
        val photoUri = currentPhoto?.uri
        if (photoUri != null) {
            allPhotos = allPhotos.filter { it.uri != photoUri }
        }
        currentPhoto = null
        
        // 更新 UI 状态
        if (allPhotos.isEmpty()) {
            _uiState.value = UiState.NoPhotos
        } else {
            _uiState.value = UiState.Success(allPhotos, null)
            // 自动加载下一张
            nextRandomPhoto()
        }
    }
    
    /**
     * 取消删除（用户取消后的回调）
     */
    fun cancelDelete() {
        // 不做任何操作，保持当前状态
    }
    
    /**
     * 设置权限被拒绝状态
     */
    fun onPermissionDenied() {
        _uiState.value = UiState.PermissionRequired
    }
    
    /**
     * 获取当前照片（用于权限授予后重新加载）
     */
    fun getCurrentPhoto(): Photo? = currentPhoto
    
    /**
     * 刷新照片列表（权限授予后调用）
     */
    fun refreshAfterPermissionGranted() {
        isLoaded = false
        loadPhotos()
    }
}
