package uz.kmax.timora.data.managers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import uz.kmax.timora.data.helper.NotificationHelper

class NotificationWorker2(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title")
        val message = inputData.getString("message")

        if (!title.isNullOrEmpty() && !message.isNullOrEmpty()) {
            NotificationHelper.showNotification(applicationContext, title, message)
            return Result.success()
        } else {
            return Result.failure()
        }
    }
}