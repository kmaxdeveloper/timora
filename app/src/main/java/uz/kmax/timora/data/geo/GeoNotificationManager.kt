package uz.kmax.timora.data.geo

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import uz.kmax.timora.data.broadcast.GeoFenceReceiverBot
import uz.kmax.timora.data.broadcast.GeofenceReceiver

class GeoNotificationManager {

    fun addGeoFence(context: Context,latitude : Double,longitude : Double,onComplete: (Boolean) -> Unit = {}){
        val geofence = Geofence.Builder()
            .setRequestId("unique_geofence_id")
            .setCircularRegion(latitude,longitude,350f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(context,GeofenceReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        val geofencingClient = LocationServices.getGeofencingClient(context)

        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            geofencingClient.addGeofences(geofenceRequest,pendingIntent)
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener {message->
                    onComplete(false)
                    Toast.makeText(context, message.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }
}