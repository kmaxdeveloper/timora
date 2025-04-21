package uz.kmax.timora.data.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import uz.kmax.timora.R
import kotlin.random.Random

class GeoFenceReceiverBot: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofenceEvent = GeofencingEvent.fromIntent(intent)
        if (geofenceEvent!!.hasError()) return

        val transitionType = geofenceEvent.geofenceTransition
        val transitionString = when(transitionType){
            Geofence.GEOFENCE_TRANSITION_ENTER-> "Hududga kirdingiz !"
            Geofence.GEOFENCE_TRANSITION_EXIT-> "Hududdan chiqdingiz !"
            else-> return
        }

        val notificationManager  = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "geo_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,"Geo Channel",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context,channelId)
            .setContentText("Siz Hududga kirdingiz !")
            .setContentText(transitionString)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        val channelRandom = Random.nextInt(99999)
        notificationManager.notify(channelRandom,notification)
    }

}