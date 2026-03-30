package com.randomphoto.app.data

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 照片数据模型
 * @param uri 照片的 URI
 * @param displayName 照片显示名称
 * @param dateAdded 添加日期
 * @param width 宽度
 * @param height 高度
 */
data class Photo(
    val uri: Uri,
    val displayName: String,
    val dateAdded: Long,
    val width: Int = 0,
    val height: Int = 0
)

/**
 * 照片仓库 - 负责照片的加载和删除
 * 使用 MediaStore API 访问系统相册
 */
class PhotoRepository(private val context: Context) {
    
    private val contentResolver: ContentResolver = context.contentResolver
    
    /**
     * 加载所有照片
     * @return 照片列表
     */
    suspend fun loadAllPhotos(): List<Photo> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<Photo>()
        
        // 查询外部存储中的图片
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        
        // 定义需要查询的列
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE
        )
        
        // 按添加日期降序排序（最新的在前）
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        
        try {
            contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val mimeType = cursor.getString(mimeTypeColumn)
                    
                    // 只处理图片文件
                    if (mimeType.startsWith("image/")) {
                        val photoUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        
                        photos.add(
                            Photo(
                                uri = photoUri,
                                displayName = displayName,
                                dateAdded = dateAdded,
                                width = width,
                                height = height
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        photos
    }
    
    /**
     * 删除照片结果
     */
    sealed class DeleteResult {
        object Success : DeleteResult()
        data class NeedsConfirmation(val intentSender: IntentSender) : DeleteResult()
        data class Failure(val error: String) : DeleteResult()
    }
    
    /**
     * 删除照片
     * @param photo 要删除的照片
     * @return 删除结果（成功/需要确认/失败）
     */
    suspend fun deletePhoto(photo: Photo): DeleteResult = withContext(Dispatchers.IO) {
        try {
            // Android 10+ 使用 MediaStore.createDeleteRequest() 需要用户确认
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    // 尝试直接删除（如果是应用自己创建的照片）
                    val rowsDeleted = contentResolver.delete(photo.uri, null, null)
                    if (rowsDeleted > 0) {
                        DeleteResult.Success
                    } else {
                        // 需要用户确认的系统删除请求
                        val pendingIntent = MediaStore.createDeleteRequest(
                            contentResolver,
                            listOf(photo.uri)
                        )
                        DeleteResult.NeedsConfirmation(pendingIntent.intentSender)
                    }
                } catch (e: RecoverableSecurityException) {
                    // 需要用户确认权限
                    val pendingIntent = MediaStore.createDeleteRequest(
                        contentResolver,
                        listOf(photo.uri)
                    )
                    DeleteResult.NeedsConfirmation(pendingIntent.intentSender)
                }
            } else {
                // Android 9 及以下：直接删除文件
                val file = File(photo.uri.path ?: return@withContext DeleteResult.Failure("无效的文件路径"))
                if (file.exists()) {
                    if (file.delete()) {
                        DeleteResult.Success
                    } else {
                        DeleteResult.Failure("删除失败，请检查权限")
                    }
                } else {
                    DeleteResult.Failure("文件不存在")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DeleteResult.Failure("删除异常：${e.message}")
        }
    }
    
    /**
     * 获取照片的文件路径（用于分享等场景）
     * @param photo 照片
     * @return 文件 URI
     */
    fun getPhotoFileUri(photo: Photo): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0+ 使用 FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                File(photo.uri.path ?: "")
            )
        } else {
            photo.uri
        }
    }
}
