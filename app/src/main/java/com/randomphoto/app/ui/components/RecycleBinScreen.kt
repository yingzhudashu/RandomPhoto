package com.randomphoto.app.ui.components

import android.net.Uri
import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.randomphoto.app.data.RecycleBinPhoto
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 回收站页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    photos: List<RecycleBinPhoto>,
    totalSize: Long,
    isLoading: Boolean,
    message: String?,
    onBack: () -> Unit,
    onRestore: (RecycleBinPhoto) -> Unit,
    onPermanentDelete: (RecycleBinPhoto) -> Unit,
    onClearAll: () -> Unit,
    onMessageDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<RecycleBinPhoto?>(null) }

    // 消息 Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageDismissed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("回收站", fontWeight = FontWeight.Bold)
                        Text(
                            "${photos.size} 张照片 · ${Formatter.formatFileSize(context, totalSize)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (photos.isNotEmpty()) {
                        TextButton(onClick = { showClearDialog = true }) {
                            Text("清空", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (photos.isEmpty()) {
                // 空状态
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🗑️", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "回收站是空的",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "删除的照片会在这里保留 30 天",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 提示
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "💡 照片将在 30 天后自动永久删除",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    items(photos, key = { it.id }) { photo ->
                        RecycleBinPhotoItem(
                            photo = photo,
                            onRestore = { onRestore(photo) },
                            onDelete = { showDeleteDialog = photo }
                        )
                    }
                }
            }
        }
    }

    // 清空确认对话框
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("清空回收站") },
            text = { Text("确定要永久删除回收站中的所有照片吗？此操作不可恢复。") },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDialog = false
                        onClearAll()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("清空")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 单个删除确认对话框
    showDeleteDialog?.let { photo ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("永久删除") },
            text = { Text("确定要永久删除 \"${photo.originalName}\" 吗？此操作不可恢复。") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = null
                        onPermanentDelete(photo)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("永久删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 回收站照片列表项
 */
@Composable
fun RecycleBinPhotoItem(
    photo: RecycleBinPhoto,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }
    val deletedTimeStr = remember(photo.deletedTime) { dateFormat.format(Date(photo.deletedTime)) }
    val fileSizeStr = remember(photo.fileSize) { Formatter.formatFileSize(context, photo.fileSize) }
    
    // 计算剩余天数
    val remainingDays = remember(photo.deletedTime) {
        val elapsed = System.currentTimeMillis() - photo.deletedTime
        val remaining = 30 - (elapsed / (24 * 60 * 60 * 1000))
        remaining.coerceAtLeast(0)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 缩略图
            AsyncImage(
                model = File(photo.filePath),
                contentDescription = photo.originalName,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    photo.originalName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "删除于 $deletedTimeStr · $fileSizeStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    "${remainingDays} 天后自动删除",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (remainingDays <= 3) MaterialTheme.colorScheme.error 
                           else MaterialTheme.colorScheme.outline
                )
            }

            // 操作按钮
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 恢复按钮
                FilledTonalButton(
                    onClick = onRestore,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text("恢复", fontSize = 12.sp)
                }
                // 永久删除
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除", fontSize = 12.sp)
                }
            }
        }
    }
}
