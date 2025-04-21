package uz.kmax.timora.data.geo

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import uz.kmax.timora.data.broadcast.GeofenceReceiver
import java.util.UUID

object GeoNotificationManager3 {

    private const val GEOFENCE_RADIUS = 350f
    private const val GEOFENCE_EXPIRATION = Geofence.NEVER_EXPIRE

    fun safelyAddGeoFence(
        context: Context,
        latitude: Double,
        longitude: Double,
        requestId: String = UUID.randomUUID().toString(),
        onComplete: (Boolean) -> Unit = {}
    ) {
        val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

        val geofence = Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS)
            .setExpirationDuration(GEOFENCE_EXPIRATION)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(context, GeofenceReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val hasFineLocation = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasBackgroundLocation = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation && hasBackgroundLocation) {
            // Avval eski geozonalarni o'chiramiz
            geofencingClient.removeGeofences(pendingIntent).addOnCompleteListener {
                // So'ng yangi geozona qo'shamiz
                geofencingClient.addGeofences(geofenceRequest, pendingIntent)
                    .addOnSuccessListener {
                        Toast.makeText(context, "📍 Geozona qo'shildi", Toast.LENGTH_SHORT).show()
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        if (e is ApiException) {
                            Log.e("GEOFENCE", "API Xatolik: ${e.statusCode}")
                        }
                        Toast.makeText(context, "⚠️ Geozona xatolik: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        onComplete(false)
                    }
            }
        } else {
            Toast.makeText(context, "❌ Ruxsatlar yetarli emas", Toast.LENGTH_LONG).show()
            onComplete(false)
        }
    }

    fun removeAllGeofences(context: Context, onRemoved: (Boolean) -> Unit = {}) {
        val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
        val intent = Intent(context, GeofenceReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener {
                Toast.makeText(context, "🗑️ Barcha geozonalar olib tashlandi", Toast.LENGTH_SHORT).show()
                onRemoved(true)
            }
            .addOnFailureListener {
                Toast.makeText(context, "❌ Geozonalarni olib tashlashda xatolik", Toast.LENGTH_LONG).show()
                onRemoved(false)
            }
    }
}