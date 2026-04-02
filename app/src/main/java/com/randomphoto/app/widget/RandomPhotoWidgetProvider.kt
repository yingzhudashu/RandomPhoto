package com.randomphoto.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.RemoteViews
import com.randomphoto.app.MainActivity
import com.randomphoto.app.R
import com.randomphoto.app.data.Photo
import com.randomphoto.app.data.RecycleBinManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 随机照片桌面大组件 (4x3)
 */
class RandomPhotoWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TAG = "RandomPhotoWidget"
        private const val PREFS_NAME = "widget_prefs"
        private const val HIDE_DELAY_MS = 30000L

        const val ACTION_SHOW_RANDOM_PHOTO = "com.randomphoto.SHOW_RANDOM_PHOTO"
        const val ACTION_DELETE_PHOTO = "com.randomphoto.DELETE_PHOTO"
        const val ACTION_OPEN_APP = "com.randomphoto.OPEN_APP"
        const val ACTION_HIDE_PHOTO = "com.randomphoto.HIDE_PHOTO"
        const val ACTION_OPEN_RECYCLE_BIN = "com.randomphoto.OPEN_RECYCLE_BIN"

        private val hideHandler = Handler(Looper.getMainLooper())
        private val hideRunnables = mutableMapOf<Int, Runnable>()
        private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        fun getPrefs(context: Context): SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        fun getCurrentPhotoUri(context: Context): String? =
            getPrefs(context).getString("current_photo_uri", null)

        fun getCurrentPhotoName(context: Context): String? =
            getPrefs(context).getString("current_photo_name", null)

        fun getCurrentPhotoId(context: Context): Long =
            getPrefs(context).getLong("current_photo_id", -1)

        fun saveCurrentPhoto(context: Context, uri: String, name: String, mediaId: Long) {
            getPrefs(context).edit()
                .putString("current_photo_uri", uri)
                .putString("current_photo_name", name)
                .putLong("current_photo_id", mediaId)
                .apply()
        }

        fun clearCurrentPhoto(context: Context) {
            getPrefs(context).edit()
                .remove("current_photo_uri")
                .remove("current_photo_name")
                .remove("current_photo_id")
                .apply()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        try {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            when (intent.action) {
                ACTION_SHOW_RANDOM_PHOTO -> {
                    if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        showRandomPhoto(context, appWidgetId)
                        scheduleAutoHide(context, appWidgetId)
                    }
                }
                ACTION_DELETE_PHOTO -> {
                    if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        deleteViaActivity(context, appWidgetId)
                    }
                }
                ACTION_HIDE_PHOTO -> {
                    if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        hidePhoto(context, appWidgetId)
                    }
                }
                ACTION_OPEN_APP -> {
                    val mediaId = getCurrentPhotoId(context)
                    val openIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("photo_media_id", mediaId)
                    }
                    context.startActivity(openIntent)
                }
                ACTION_OPEN_RECYCLE_BIN -> {
                    val recycleBinIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("open_recycle_bin", true)
                    }
                    context.startActivity(recycleBinIntent)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onReceive error", e)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) showHiddenState(context, id)
    }

    private fun showRandomPhoto(context: Context, appWidgetId: Int) {
        try {
            val photos = getAllPhotos(context)
            if (photos.isEmpty()) return
            val (uri, name, mediaId) = photos.random()
            saveCurrentPhoto(context, uri.toString(), name, mediaId)
            val bitmap = loadScaledBitmap(context, uri, 800, 800) ?: return
            val views = RemoteViews(context.packageName, R.layout.widget_random_photo_large)
            views.setImageViewBitmap(R.id.widget_photo, bitmap)
            views.setTextViewText(R.id.widget_photo_info, name.take(15))
            setupButtons(context, views, appWidgetId)
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views)
        } catch (e: Exception) {
            Log.e(TAG, "showRandomPhoto error", e)
        }
    }

    /**
     * 通过 TrashRequestActivity 处理删除（两步：先复制到回收站，再系统确认删除）
     */
    private fun deleteViaActivity(context: Context, appWidgetId: Int) {
        val uriStr = getCurrentPhotoUri(context) ?: return
        val name = getCurrentPhotoName(context) ?: "unknown"
        val mediaId = getCurrentPhotoId(context)
        if (mediaId <= 0) return

        // 先在后台复制到回收站
        widgetScope.launch {
            try {
                val recycleBinManager = RecycleBinManager(context)
                val photoUri = Uri.parse(uriStr)
                val photo = Photo(uri = photoUri, displayName = name, dateAdded = 0)
                
                val recycleBinId = recycleBinManager.copyToRecycleBin(photo)
                if (recycleBinId == null) {
                    Log.e(TAG, "Failed to copy to recycle bin")
                    return@launch
                }

                // 启动 TrashRequestActivity 处理系统删除确认
                val trashIntent = Intent(context, TrashRequestActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("photo_uri", uriStr)
                    putExtra("recycle_bin_id", recycleBinId)
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }
                context.startActivity(trashIntent)
            } catch (e: Exception) {
                Log.e(TAG, "deleteViaActivity error", e)
            }
        }
    }

    private fun hidePhoto(context: Context, appWidgetId: Int) {
        cancelAutoHide(appWidgetId)
        showHiddenState(context, appWidgetId)
    }

    private fun showHiddenState(context: Context, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_random_photo_large)
        views.setImageViewResource(R.id.widget_photo, 0)
        views.setTextViewText(R.id.widget_photo_info, "点击随机加载")
        setupButtons(context, views, appWidgetId)
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views)
    }

    private fun setupButtons(context: Context, views: RemoteViews, appWidgetId: Int) {
        views.setOnClickPendingIntent(R.id.widget_random_button,
            makePendingIntent(context, ACTION_SHOW_RANDOM_PHOTO, appWidgetId, 0))
        views.setOnClickPendingIntent(R.id.widget_photo,
            makePendingIntent(context, ACTION_SHOW_RANDOM_PHOTO, appWidgetId, 0))
        views.setOnClickPendingIntent(R.id.widget_delete_button,
            makePendingIntent(context, ACTION_DELETE_PHOTO, appWidgetId, 200))
        views.setOnClickPendingIntent(R.id.widget_hide_button,
            makePendingIntent(context, ACTION_HIDE_PHOTO, appWidgetId, 300))
        views.setOnClickPendingIntent(R.id.widget_recycle_bin_button,
            makePendingIntent(context, ACTION_OPEN_RECYCLE_BIN, appWidgetId, 400))
        views.setOnClickPendingIntent(R.id.widget_layout,
            makePendingIntent(context, ACTION_OPEN_APP, appWidgetId, 1000))
    }

    private fun scheduleAutoHide(context: Context, appWidgetId: Int) {
        cancelAutoHide(appWidgetId)
        val runnable = Runnable { hidePhoto(context, appWidgetId) }
        hideRunnables[appWidgetId] = runnable
        hideHandler.postDelayed(runnable, HIDE_DELAY_MS)
    }

    private fun cancelAutoHide(appWidgetId: Int) {
        hideRunnables[appWidgetId]?.let { hideHandler.removeCallbacks(it) }
        hideRunnables.remove(appWidgetId)
    }

    private fun getAllPhotos(context: Context): List<Triple<Uri, String, Long>> {
        val photos = mutableListOf<Triple<Uri, String, Long>>()
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val name = cursor.getString(nameCol)
                photos.add(Triple(
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id),
                    name, id
                ))
            }
        }
        return photos
    }

    private fun loadScaledBitmap(context: Context, uri: Uri, maxW: Int, maxH: Int): android.graphics.Bitmap? {
        return try {
            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            val scale = Math.max(opts.outWidth / maxW, opts.outHeight / maxH).coerceAtLeast(1)
            val scaledOpts = BitmapFactory.Options().apply { inSampleSize = scale }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, scaledOpts) }
        } catch (e: Exception) {
            Log.e(TAG, "loadScaledBitmap error", e)
            null
        }
    }

    private fun makePendingIntent(context: Context, action: String, appWidgetId: Int, offset: Int): PendingIntent {
        val intent = Intent(context, RandomPhotoWidgetProvider::class.java).apply {
            this.action = action
            setPackage(context.packageName)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        return PendingIntent.getBroadcast(
            context, appWidgetId + offset, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
