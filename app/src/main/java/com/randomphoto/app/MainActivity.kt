package com.randomphoto.app

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * 主活动 - 应用入口
 */
class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadPhotos()
        } else {
            viewModel.onPermissionDenied()
        }
    }
    
    internal val deleteConfirmLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.completeDelete()
            Toast.makeText(this, "照片已删除", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.cancelDelete()
            Toast.makeText(this, "已取消删除", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val viewModel: MainViewModel by lazy {
        MainViewModel(application)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleWidgetSync(intent)
        
        setContent {
            RandomPhotoAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RandomPhotoApp(
                            viewModel = viewModel,
                            deleteConfirmLauncher = deleteConfirmLauncher
                        )
                    }
                }
            }
        }
        
        checkAndRequestPermission()
    }
    
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        handleWidgetSync(intent)
    }
    
    override fun onResume() {
        super.onResume()
        syncFromWidgetPrefs()
        // 检查同步状态（检测小组件端的删除/恢复操作）
        viewModel.checkSyncState()
    }
    
    private fun handleWidgetSync(intent: android.content.Intent?) {
        val mediaId = intent?.getLongExtra("photo_media_id", -1L) ?: -1L
        if (mediaId > 0) {
            val photoUri = android.content.ContentUris.withAppendedId(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaId
            )
            viewModel.showSpecificPhoto(photoUri)
        }
        
        // 处理打开回收站的 Intent
        val openRecycleBin = intent?.getBooleanExtra("open_recycle_bin", false) ?: false
        if (openRecycleBin) {
            // 通过 SharedPreferences 标记需要打开回收站
            getSharedPreferences("widget_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("should_open_recycle_bin", true)
                .apply()
        }
    }
    
    private fun syncFromWidgetPrefs() {
        val prefs = getSharedPreferences("widget_prefs", MODE_PRIVATE)
        val mediaId = prefs.getLong("current_photo_id", -1)
        if (mediaId > 0) {
            val photoUri = android.content.ContentUris.withAppendedId(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaId
            )
            viewModel.showSpecificPhoto(photoUri)
        }
    }
    
    internal fun checkAndRequestPermission() {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Manifest.permission.READ_MEDIA_IMAGES
            }
            else -> Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.loadPhotos()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}

/**
 * 应用主界面（包含回收站导航）
 */
@Composable
fun RandomPhotoApp(
    viewModel: MainViewModel = viewModel(),
    deleteConfirmLauncher: ActivityResultLauncher<IntentSenderRequest>
) {
    val uiState by viewModel.uiState.collectAsState()
    val recycleBinState by viewModel.recycleBinState.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showWidgetGuide by remember { mutableStateOf(false) }
    
    // 是否在回收站页面
    var showRecycleBin by remember { mutableStateOf(false) }
    
    // 检查是否需要自动打开回收站
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("widget_prefs", android.content.Context.MODE_PRIVATE)
        if (prefs.getBoolean("should_open_recycle_bin", false)) {
            prefs.edit().remove("should_open_recycle_bin").apply()
            showRecycleBin = true
            viewModel.loadRecycleBin()
        }
    }
    
    val currentPhotoUri by derivedStateOf {
        when (val state = uiState) {
            is UiState.Success -> state.currentPhoto?.uri
            else -> null
        }
    }
    
    val lifecycleOwner = LocalLifecycleOwner.current
    
    if (showWidgetGuide) {
        AlertDialog(
            onDismissRequest = { showWidgetGuide = false },
            title = { Text("添加桌面小组件") },
            text = { Text("请长按桌面空白处 → 选择'小组件' → 找到'随机照片' → 拖动到桌面") },
            confirmButton = {
                TextButton(onClick = { showWidgetGuide = false }) { Text("知道了") }
            }
        )
    }
    
    // 回收站页面
    if (showRecycleBin) {
        RecycleBinScreen(
            photos = recycleBinState.photos,
            totalSize = recycleBinState.totalSize,
            isLoading = recycleBinState.isLoading,
            message = recycleBinState.message,
            onBack = { showRecycleBin = false },
            onRestore = { viewModel.restoreFromRecycleBin(it) },
            onPermanentDelete = { viewModel.permanentDeleteFromRecycleBin(it) },
            onClearAll = { viewModel.clearRecycleBin() },
            onMessageDismissed = { viewModel.clearRecycleBinMessage() }
        )
        return
    }
    
    // 主页面
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部按钮行：添加桌面小组件 + 回收站（水平对齐）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 添加桌面小组件按钮
                Button(
                    onClick = { showWidgetGuide = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text("📌", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("添加小组件", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                
                // 回收站按钮
                OutlinedButton(
                    onClick = {
                        showRecycleBin = true
                        viewModel.loadRecycleBin()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text("🗑️", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("回收站", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
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
                            onRetry = { viewModel.refreshAfterPermissionGranted() }
                        )
                    }
                }
            }
            
            // 底部操作按钮
            if (uiState is UiState.Success) {
                val successState = uiState as UiState.Success
                ActionButtons(
                    onNextClick = { viewModel.nextRandomPhoto() },
                    onDeleteClick = { showDeleteDialog = true },
                    canDelete = successState.currentPhoto != null
                )
            }
        }
        
        // 删除确认对话框（移到回收站）
        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                onDismissRequest = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    lifecycleOwner.lifecycleScope.launch {
                        val result = viewModel.deleteCurrentPhoto()
                        when (result) {
                            is MainViewModel.DeleteRequestResult.Success -> {
                                Toast.makeText(context, "🗑️ 已移到回收站", Toast.LENGTH_SHORT).show()
                            }
                            is MainViewModel.DeleteRequestResult.NeedsConfirmation -> {
                                deleteConfirmLauncher.launch(
                                    IntentSenderRequest.Builder(result.intentSender).build()
                                )
                            }
                            is MainViewModel.DeleteRequestResult.Failure -> {
                                Toast.makeText(context, "删除失败: ${result.error}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        }
    }
}
