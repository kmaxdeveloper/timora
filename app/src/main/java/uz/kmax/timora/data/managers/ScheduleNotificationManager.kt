package uz.kmax.timora.data.managers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import uz.kmax.timora.data.broadcast.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.Locale

object ScheduleNotificationManager {
    @SuppressLint("ScheduleExactAlarm")
    fun setScheduleNotification(context: Context, timeString: String?, millisTime: Long?, title: String, message: String) {
        val triggerTime = when {
            millisTime != null -> millisTime
            !timeString.isNullOrBlank() -> {
                try {
                    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val date = formatter.parse(timeString)
                    date?.time
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            else -> null
        }

        if (triggerTime == null) {
            Toast.makeText(context, "Vaqt noto‘g‘ri kiritilgan", Toast.LENGTH_SHORT).show()
            return
        }

        if (triggerTime < System.currentTimeMillis()) {
            Toast.makeText(context, "O‘tgan vaqtni kiritib bo‘lmaydi!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(), // har xil ID bo‘lishi uchun
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager : AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )

        Toast.makeText(context, "Eslatma belgilandi!", Toast.LENGTH_SHORT).show()
    }
}
