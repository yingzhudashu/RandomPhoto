package com.randomphoto.app.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 照片同步管理器
 * 
 * 处理应用内和小组件之间的照片同步，
 * 包括检测已删除的照片（从相册或回收站中删除后的同步更新）
 */
class PhotoSyncManager(private val context: Context) {

    companion object {
        private const val TAG = "PhotoSyncManager"
        private const val PREFS_NAME = "widget_prefs"
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 获取当前同步版本号
     */
    fun getSyncVersion(): Int = prefs.getInt("sync_version", 0)

    /**
     * 获取最后变更时间
     */
    fun getLastChangeTime(): Long = prefs.getLong("last_change_time", 0)

    /**
     * 记录应用内当前显示的照片
     */
    fun setAppCurrentPhoto(mediaId: Long) {
        prefs.edit()
            .putLong("app_current_photo_id", mediaId)
            .putLong("app_last_update", System.currentTimeMillis())
            .apply()
    }

    /**
     * 获取应用内当前照片 ID
     */
    fun getAppCurrentPhotoId(): Long = prefs.getLong("app_current_photo_id", -1)

    /**
     * 记录小组件当前显示的照片
     */
    fun setWidgetCurrentPhoto(mediaId: Long) {
        prefs.edit()
            .putLong("current_photo_id", mediaId)
            .putLong("widget_last_update", System.currentTimeMillis())
            .apply()
    }

    /**
     * 获取小组件当前照片 ID
     */
    fun getWidgetCurrentPhotoId(): Long = prefs.getLong("current_photo_id", -1)

    /**
     * 通知照片已被删除（移入回收站）
     * 通知所有观察者
     */
    fun notifyPhotoDeleted(mediaId: Long) {
        val version = getSyncVersion() + 1
        prefs.edit()
            .putInt("sync_version", version)
            .putLong("last_deleted_photo_id", mediaId)
            .putLong("last_change_time", System.currentTimeMillis())
            .putString("last_action", "delete")
            .apply()
    }

    /**
     * 通知照片已恢复
     */
    fun notifyPhotoRestored() {
        val version = getSyncVersion() + 1
        prefs.edit()
            .putInt("sync_version", version)
            .putLong("last_change_time", System.currentTimeMillis())
            .putString("last_action", "restore")
            .apply()
    }

    /**
     * 获取最后一次被删除的照片 ID
     */
    fun getLastDeletedPhotoId(): Long = prefs.getLong("last_deleted_photo_id", -1)

    /**
     * 获取最后一次操作类型
     */
    fun getLastAction(): String? = prefs.getString("last_action", null)

    /**
     * 检查某个照片是否还存在于 MediaStore
     */
    suspend fun isPhotoStillExists(mediaId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mediaId
            )
            val cursor = context.contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.Media._ID),
                null, null, null
            )
            val exists = cursor?.use { it.count > 0 } ?: false
            exists
        } catch (e: Exception) {
            Log.e(TAG, "isPhotoStillExists check failed", e)
            false
        }
    }

    /**
     * 清理同步状态
     */
    fun clearSyncState() {
        prefs.edit()
            .remove("last_deleted_photo_id")
            .remove("last_action")
            .apply()
    }
}
