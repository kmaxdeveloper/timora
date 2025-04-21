package uz.kmax.timora.data.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import uz.kmax.timora.data.managers.NotificationWorker

class AlarmReceiver2 : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java).build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}