package com.stdy4u.study4u.service

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.stdy4u.study4u.MainActivity
import com.stdy4u.study4u.Study4UApplication
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class UpdateService : IntentService("UpdateService") {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    override fun onHandleIntent(intent: Intent?) {
        val downloadUrl = intent?.getStringExtra(EXTRA_DOWNLOAD_URL) ?: return

        try {
            showNotification("Downloading update...", 0)

            val request = Request.Builder().url(downloadUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) return

            val body = response.body ?: return
            val file = File(cacheDir, "study4u-update.apk")

            FileOutputStream(file).use { outputStream ->
                val totalBytes = body.contentLength()
                var downloadedBytes = 0L
                val buffer = ByteArray(8192)
                var bytesRead: Int

                body.byteStream().use { inputStream ->
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        val progress = if (totalBytes > 0) {
                            ((downloadedBytes * 100) / totalBytes).toInt()
                        } else 0
                        showNotification("Downloading...", progress)
                    }
                }
            }

            installApk(file)
            showNotification("Download complete", 100)
        } catch (e: Exception) {
            showNotification("Update failed: ${e.message}", 0)
        }
    }

    private fun showNotification(message: String, progress: Int) {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, Study4UApplication.CHANNEL_APP_UPDATES)
            .setContentTitle("STUDY4U Update")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_download)
            .setContentIntent(openIntent)
            .setProgress(100, progress, false)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(UPDATE_NOTIFICATION_ID, notification)
    }

    private fun installApk(apkFile: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            apkFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_DOWNLOAD_URL = "extra_download_url"
        const val UPDATE_NOTIFICATION_ID = 2001
    }
}
