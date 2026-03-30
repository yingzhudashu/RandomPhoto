package com.randomphoto.app

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.randomphoto.app.ui.components.*
import com.randomphoto.app.ui.theme.RandomPhotoAppTheme
import kotlinx.coroutines.launch

/**
 * 主活动 - 应用的入口点
 * 负责权限申请和 UI 展示
 */
class MainActivity : ComponentActivity() {
    
    // 删除请求码
    private val DELETE_REQUEST_CODE = 1001
    
    // 权限请求启动器
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 权限已授予，加载照片
            viewModel.loadPhotos()
        } else {
            // 权限被拒绝
            viewModel.onPermissionDenied()
        }
    }
    
    // 删除确认启动器（Android 10+）
    internal val deleteConfirmLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // 用户确认删除，系统已删除
            viewModel.completeDelete()
            Toast.makeText(this, "照片已删除", Toast.LENGTH_SHORT).show()
        } else {
            // 用户取消删除
            viewModel.cancelDelete()
            Toast.makeText(this, "已取消删除", Toast.LENGTH_SHORT).show()
        }
    }
    
    // ViewModel
    private val viewModel: MainViewModel by lazy {
        MainViewModel(application)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            RandomPhotoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RandomPhotoApp(
                        viewModel = viewModel,
                        deleteConfirmLauncher = deleteConfirmLauncher
                    )
                }
            }
        }
        
        // 检查并请求权限
        checkAndRequestPermission()
    }
    
    /**
     * 检查相册权限并请求
     */
    internal fun checkAndRequestPermission() {
        val permission = when {
            // Android 13+ (API 33+) 使用 READ_MEDIA_IMAGES
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Manifest.permission.READ_MEDIA_IMAGES
            }
            // Android 12 及以下使用 READ_EXTERNAL_STORAGE
            else -> Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        when {
            // 权限已授予
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.loadPhotos()
            }
            // 需要请求权限
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}

/**
 * 应用主界面
 * @param viewModel MainViewModel
 * @param deleteConfirmLauncher 删除确认启动器
 */
@Composable
fun RandomPhotoApp(
    viewModel: MainViewModel = viewModel(),
    deleteConfirmLauncher: androidx.activity.result.ActivityResultLauncher<IntentSenderRequest>
) {
    // 收集 UI 状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 删除确认对话框状态
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // 当前照片 URI
    val currentPhotoUri by derivedStateOf {
        when (val state = uiState) {
            is UiState.Success -> state.currentPhoto?.uri
            else -> null
        }
    }
    
    // 用于在 Composable 中启动协程
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 照片显示区域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        LoadingState()
                    }
                    is UiState.PermissionRequired -> {
                        PermissionRequiredState(
                            onGrantPermission = {
                                // 重新请求权限
                                if (context is MainActivity) {
                                    context.checkAndRequestPermission()
                                }
                            }
                        )
                    }
                    is UiState.Success -> {
                        if (state.photos.isEmpty()) {
                            EmptyState()
                        } else {
                            PhotoDisplay(photoUri = currentPhotoUri)
                        }
                    }
                    is UiState.NoPhotos -> {
                        EmptyState()
                    }
                    is UiState.Error -> {
                        ErrorState(
                            message = state.message,
                            onRetry = {
                                viewModel.refreshAfterPermissionGranted()
                            }
                        )
                    }
                }
            }
            
            // 底部操作按钮
            if (uiState is UiState.Success) {
                val successState = uiState as UiState.Success
                ActionButtons(
                    onNextClick = {
                        viewModel.nextRandomPhoto()
                    },
                    onDeleteClick = {
                        showDeleteDialog = true
                    },
                    canDelete = successState.currentPhoto != null
                )
            }
        }
        
        // 删除确认对话框
        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                onDismissRequest = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    // 执行删除
                    lifecycleOwner.lifecycleScope.launch {
                        val result = viewModel.deleteCurrentPhoto()
                        when (result) {
                            is MainViewModel.DeleteRequestResult.Success -> {
                                // 已删除成功
                                Toast.makeText(context, "照片已删除", Toast.LENGTH_SHORT).show()
                            }
                            is MainViewModel.DeleteRequestResult.NeedsConfirmation -> {
                                // Android 10+：启动系统确认对话框
                                deleteConfirmLauncher.launch(
                                    IntentSenderRequest.Builder(result.intentSender).build()
                                )
                            }
                            is MainViewModel.DeleteRequestResult.Failure -> {
                                // 删除失败，错误信息已在 ViewModel 中显示
                            }
                        }
                    }
                }
            )
        }
    }
}
