package uz.kmax.timora.data.managers

import android.content.Context
import android.widget.Toast
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object DelayNotification {

    fun startDelayedNotification(context: Context, title: String, message: String, delayMillis: Long) {
        val data = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .build()

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(notificationWorkRequest)
        Toast.makeText(context, "Bildirishnoma ${delayMillis / 1000} soniyadan keyin ko'rsatiladi.", Toast.LENGTH_SHORT).show()
    }
}