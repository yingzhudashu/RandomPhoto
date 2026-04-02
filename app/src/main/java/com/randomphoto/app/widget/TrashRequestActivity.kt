package com.randomphoto.app.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.randomphoto.app.data.RecycleBinManager

/**
 * 透明 Activity - 处理系统删除确认
 * 
 * 流程：
 * 1. 收到 photo_uri 和 recycle_bin_id
 * 2. 弹出系统删除确认框
 * 3. 确认 → confirmMove + 通知小组件刷新
 * 4. 取消 → rollbackCopy
 */
class TrashRequestActivity : ComponentActivity() {

    companion object {
        private const val TAG = "TrashRequestActivity"
    }

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var recycleBinId: String? = null
    private lateinit var recycleBinManager: RecycleBinManager

    private val deleteLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        val rbId = recycleBinId
        if (result.resultCode == RESULT_OK && rbId != null) {
            // 用户确认删除
            recycleBinManager.confirmMove(rbId)
            
            // 更新同步状态
            val prefs = getSharedPreferences("widget_prefs", MODE_PRIVATE)
            val version = prefs.getInt("sync_version", 0) + 1
            prefs.edit()
                .putInt("sync_version", version)
                .putLong("last_change_time", System.currentTimeMillis())
                .putString("last_action", "delete")
                .apply()
            
            RandomPhotoWidgetProvider.clearCurrentPhoto(this)
            Toast.makeText(this, "🗑️ 已移到回收站", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Photo deleted and moved to recycle bin")
        } else if (rbId != null) {
            // 用户取消
            recycleBinManager.rollbackCopy(rbId)
            Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "User cancelled, rolled back copy")
        }

        // 通知小组件加载下一张
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            val nextIntent = Intent(this, RandomPhotoWidgetProvider::class.java).apply {
                action = RandomPhotoWidgetProvider.ACTION_SHOW_RANDOM_PHOTO
                setPackage(packageName)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            sendBroadcast(nextIntent)
        }

        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recycleBinManager = RecycleBinManager(this)

        val photoUriStr = intent?.getStringExtra("photo_uri")
        recycleBinId = intent?.getStringExtra("recycle_bin_id")
        appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (photoUriStr == null || recycleBinId == null) {
            Log.e(TAG, "Missing photo_uri or recycle_bin_id")
            finish()
            return
        }

        val photoUri = Uri.parse(photoUriStr)
        requestSystemDelete(photoUri)
    }

    private fun requestSystemDelete(photoUri: Uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+：createDeleteRequest
                val deleteRequest = MediaStore.createDeleteRequest(
                    contentResolver,
                    listOf(photoUri)
                )
                deleteLauncher.launch(
                    IntentSenderRequest.Builder(deleteRequest.intentSender).build()
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10：尝试直接删除
                try {
                    val rows = contentResolver.delete(photoUri, null, null)
                    if (rows > 0) {
                        recycleBinId?.let { recycleBinManager.confirmMove(it) }
                        
                        val prefs = getSharedPreferences("widget_prefs", MODE_PRIVATE)
                        val version = prefs.getInt("sync_version", 0) + 1
                        prefs.edit()
                            .putInt("sync_version", version)
                            .putLong("last_change_time", System.currentTimeMillis())
                            .putString("last_action", "delete")
                            .apply()
                        
                        RandomPhotoWidgetProvider.clearCurrentPhoto(this)
                        Toast.makeText(this, "🗑️ 已移到回收站", Toast.LENGTH_SHORT).show()
                    } else {
                        // 尝试 createDeleteRequest
                        val deleteRequest = MediaStore.createDeleteRequest(
                            contentResolver,
                            listOf(photoUri)
                        )
                        deleteLauncher.launch(
                            IntentSenderRequest.Builder(deleteRequest.intentSender).build()
                        )
                        return
                    }
                } catch (e: android.app.RecoverableSecurityException) {
                    deleteLauncher.launch(
                        IntentSenderRequest.Builder(e.userAction.actionIntent.intentSender).build()
                    )
                    return
                }

                // 通知小组件
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val nextIntent = Intent(this, RandomPhotoWidgetProvider::class.java).apply {
                        action = RandomPhotoWidgetProvider.ACTION_SHOW_RANDOM_PHOTO
                        setPackage(packageName)
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }
                    sendBroadcast(nextIntent)
                }
                finish()
            } else {
                // Android 9 及以下
                val rows = contentResolver.delete(photoUri, null, null)
                if (rows > 0) {
                    recycleBinId?.let { recycleBinManager.confirmMove(it) }
                    Toast.makeText(this, "🗑️ 已删除", Toast.LENGTH_SHORT).show()
                } else {
                    recycleBinId?.let { recycleBinManager.rollbackCopy(it) }
                    Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
                }

                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val nextIntent = Intent(this, RandomPhotoWidgetProvider::class.java).apply {
                        action = RandomPhotoWidgetProvider.ACTION_SHOW_RANDOM_PHOTO
                        setPackage(packageName)
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }
                    sendBroadcast(nextIntent)
                }
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "requestSystemDelete failed", e)
            recycleBinId?.let { recycleBinManager.rollbackCopy(it) }
            Toast.makeText(this, "❌ 操作失败: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
