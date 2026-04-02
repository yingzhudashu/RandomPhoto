package com.randomphoto.app.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * 回收站照片数据模型
 */
data class RecycleBinPhoto(
    val id: String,
    val originalName: String,
    val deletedTime: Long,
    val filePath: String,
    val originalMediaId: Long,
    val fileSize: Long = 0
)

/**
 * 自定义回收站管理器
 * 
 * 两步删除流程：
 * 1. copyToRecycleBin() — 复制文件到 app 私有目录 + 记录元数据
 * 2. 由调用方处理 MediaStore 删除（需要系统确认对话框）
 * 3. 删除成功后调用 confirmMove() 完成流程
 * 4. 用户取消删除后调用 rollbackCopy() 回滚
 */
class RecycleBinManager(private val context: Context) {

    companion object {
        private const val TAG = "RecycleBinManager"
        private const val DIR_NAME = "recycle_bin"
        private const val METADATA_FILE = "metadata.json"
        private const val PREFS_NAME = "recycle_bin_prefs"
        private const val KEY_VERSION = "recycle_bin_version"
        private const val AUTO_CLEAN_DAYS = 30L
        private const val AUTO_CLEAN_MS = AUTO_CLEAN_DAYS * 24 * 60 * 60 * 1000
    }

    private val recycleDir: File by lazy {
        File(context.filesDir, DIR_NAME).also { it.mkdirs() }
    }

    private val metadataFile: File by lazy {
        File(recycleDir, METADATA_FILE)
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getVersion(): Int = prefs.getInt(KEY_VERSION, 0)

    private fun incrementVersion() {
        val newVersion = getVersion() + 1
        prefs.edit().putInt(KEY_VERSION, newVersion).apply()
        context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("sync_version", newVersion)
            .putLong("last_change_time", System.currentTimeMillis())
            .apply()
    }

    /**
     * 第一步：复制照片到回收站目录（不删除 MediaStore）
     * 
     * @return 回收站记录 ID，失败返回 null
     */
    suspend fun copyToRecycleBin(photo: Photo): String? = withContext(Dispatchers.IO) {
        try {
            val mediaId = android.content.ContentUris.parseId(photo.uri)
            val id = System.currentTimeMillis().toString()
            val extension = photo.displayName.substringAfterLast('.', "jpg")
            val fileName = "${id}.${extension}"
            val destFile = File(recycleDir, fileName)

            // 复制图片到回收站目录
            context.contentResolver.openInputStream(photo.uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            } ?: run {
                Log.e(TAG, "Failed to open input stream for ${photo.uri}")
                return@withContext null
            }

            // 记录元数据（标记为 pending，等待 MediaStore 删除确认）
            val recycledPhoto = RecycleBinPhoto(
                id = id,
                originalName = photo.displayName,
                deletedTime = System.currentTimeMillis(),
                filePath = destFile.absolutePath,
                originalMediaId = mediaId,
                fileSize = destFile.length()
            )
            addMetadata(recycledPhoto, pending = true)

            Log.d(TAG, "Photo copied to recycle bin: ${photo.displayName}, id=$id")
            id
        } catch (e: Exception) {
            Log.e(TAG, "copyToRecycleBin failed", e)
            null
        }
    }

    /**
     * 第二步A：MediaStore 删除成功后，确认完成
     */
    fun confirmMove(recycleBinId: String) {
        try {
            val array = loadMetadata()
            val newArray = JSONArray()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                if (obj.getString("id") == recycleBinId) {
                    obj.put("pending", false)
                    newArray.put(obj)
                } else {
                    newArray.put(obj)
                }
            }
            saveMetadata(newArray)
            incrementVersion()
            Log.d(TAG, "Move confirmed: $recycleBinId")
        } catch (e: Exception) {
            Log.e(TAG, "confirmMove failed", e)
        }
    }

    /**
     * 第二步B：用户取消了 MediaStore 删除，回滚已复制的文件
     */
    fun rollbackCopy(recycleBinId: String) {
        try {
            val array = loadMetadata()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                if (obj.getString("id") == recycleBinId) {
                    val filePath = obj.getString("filePath")
                    File(filePath).delete()
                    break
                }
            }
            removeMetadata(recycleBinId)
            Log.d(TAG, "Copy rolled back: $recycleBinId")
        } catch (e: Exception) {
            Log.e(TAG, "rollbackCopy failed", e)
        }
    }

    /**
     * 从回收站恢复照片到相册
     */
    suspend fun restorePhoto(recycleBinPhoto: RecycleBinPhoto): Boolean = withContext(Dispatchers.IO) {
        try {
            val sourceFile = File(recycleBinPhoto.filePath)
            if (!sourceFile.exists()) {
                Log.e(TAG, "Recycle bin file not found: ${recycleBinPhoto.filePath}")
                removeMetadata(recycleBinPhoto.id)
                return@withContext false
            }

            val ext = recycleBinPhoto.originalName.substringAfterLast('.', "jpg").lowercase()
            val mimeExt = when (ext) {
                "png" -> "png"; "gif" -> "gif"; "webp" -> "webp"; else -> "jpeg"
            }

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, recycleBinPhoto.originalName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/$mimeExt")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Restored")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val newUri = context.contentResolver.insert(collection, values)
            if (newUri == null) {
                Log.e(TAG, "Failed to insert into MediaStore")
                return@withContext false
            }

            context.contentResolver.openOutputStream(newUri)?.use { output ->
                sourceFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val updateValues = ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }
                context.contentResolver.update(newUri, updateValues, null, null)
            }

            sourceFile.delete()
            removeMetadata(recycleBinPhoto.id)
            incrementVersion()

            Log.d(TAG, "Photo restored: ${recycleBinPhoto.originalName}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "restorePhoto failed", e)
            false
        }
    }

    /**
     * 永久删除回收站中的照片
     */
    suspend fun permanentDelete(recycleBinPhoto: RecycleBinPhoto): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(recycleBinPhoto.filePath)
            if (file.exists()) file.delete()
            removeMetadata(recycleBinPhoto.id)
            incrementVersion()
            Log.d(TAG, "Photo permanently deleted: ${recycleBinPhoto.originalName}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "permanentDelete failed", e)
            false
        }
    }

    /**
     * 获取回收站中所有照片（不含 pending 的）
     */
    suspend fun listRecycleBinPhotos(): List<RecycleBinPhoto> = withContext(Dispatchers.IO) {
        try {
            val metadata = loadMetadata()
            val photos = mutableListOf<RecycleBinPhoto>()

            for (i in 0 until metadata.length()) {
                val obj = metadata.getJSONObject(i)
                // 跳过 pending 状态的（正在等用户确认删除）
                if (obj.optBoolean("pending", false)) continue
                
                val photo = RecycleBinPhoto(
                    id = obj.getString("id"),
                    originalName = obj.getString("originalName"),
                    deletedTime = obj.getLong("deletedTime"),
                    filePath = obj.getString("filePath"),
                    originalMediaId = obj.optLong("originalMediaId", -1),
                    fileSize = obj.optLong("fileSize", 0)
                )
                if (File(photo.filePath).exists()) {
                    photos.add(photo)
                }
            }

            photos.sortByDescending { it.deletedTime }
            photos
        } catch (e: Exception) {
            Log.e(TAG, "listRecycleBinPhotos failed", e)
            emptyList()
        }
    }

    /**
     * 清空回收站
     */
    suspend fun clearAll(): Boolean = withContext(Dispatchers.IO) {
        try {
            recycleDir.listFiles()?.forEach { file ->
                if (file.name != METADATA_FILE) file.delete()
            }
            saveMetadata(JSONArray())
            incrementVersion()
            true
        } catch (e: Exception) {
            Log.e(TAG, "clearAll failed", e)
            false
        }
    }

    suspend fun getCount(): Int = withContext(Dispatchers.IO) {
        try { loadMetadata().length() } catch (e: Exception) { 0 }
    }

    suspend fun autoCleanExpired(): Int = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            val photos = listRecycleBinPhotos()
            var cleaned = 0
            for (photo in photos) {
                if (now - photo.deletedTime > AUTO_CLEAN_MS) {
                    permanentDelete(photo)
                    cleaned++
                }
            }
            // 清理 pending 超过 1 小时的（用户可能已取消但回调丢失）
            val metadata = loadMetadata()
            for (i in metadata.length() - 1 downTo 0) {
                val obj = metadata.getJSONObject(i)
                if (obj.optBoolean("pending", false)) {
                    val deletedTime = obj.getLong("deletedTime")
                    if (now - deletedTime > 3600000) {
                        val filePath = obj.getString("filePath")
                        File(filePath).delete()
                        removeMetadata(obj.getString("id"))
                        cleaned++
                    }
                }
            }
            cleaned
        } catch (e: Exception) {
            Log.e(TAG, "autoCleanExpired failed", e)
            0
        }
    }

    suspend fun getTotalSize(): Long = withContext(Dispatchers.IO) {
        try {
            recycleDir.listFiles()
                ?.filter { it.name != METADATA_FILE }
                ?.sumOf { it.length() }
                ?: 0L
        } catch (e: Exception) { 0L }
    }

    // ========== 元数据管理 ==========

    private fun loadMetadata(): JSONArray {
        return try {
            if (metadataFile.exists()) JSONArray(metadataFile.readText()) else JSONArray()
        } catch (e: Exception) {
            Log.e(TAG, "loadMetadata failed", e)
            JSONArray()
        }
    }

    private fun saveMetadata(array: JSONArray) {
        try { metadataFile.writeText(array.toString(2)) }
        catch (e: Exception) { Log.e(TAG, "saveMetadata failed", e) }
    }

    private fun addMetadata(photo: RecycleBinPhoto, pending: Boolean = false) {
        val array = loadMetadata()
        val obj = JSONObject().apply {
            put("id", photo.id)
            put("originalName", photo.originalName)
            put("deletedTime", photo.deletedTime)
            put("filePath", photo.filePath)
            put("originalMediaId", photo.originalMediaId)
            put("fileSize", photo.fileSize)
            put("pending", pending)
        }
        array.put(obj)
        saveMetadata(array)
    }

    private fun removeMetadata(id: String) {
        val array = loadMetadata()
        val newArray = JSONArray()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            if (obj.getString("id") != id) newArray.put(obj)
        }
        saveMetadata(newArray)
    }
}
