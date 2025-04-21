package uz.kmax.timora.data.tools.manager

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import uz.kmax.timora.data.tools.firebase.FirebaseManager
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.domain.data.main.NotificationData

class GeoNotificationManager2 private constructor(
    private val context: Context,
    private val firebaseManager: FirebaseManager,
    private val sharedPref: SharedPref
) {
    companion object {
        private const val GEOFENCE_RADIUS_DEFAULT = 100f // metrda
        private const val GEOFENCE_EXPIRATION_MS = Geofence.NEVER_EXPIRE
        const val ACTION_GEOFENCE_TRANSITION = "uz.kmax.timora.GEOFENCE_TRANSITION_ACTION"

        @Volatile
        private var instance: GeoNotificationManager2? = null

        fun getInstance(
            context: Context,
            firebaseManager: FirebaseManager,
            sharedPref: SharedPref
        ): GeoNotificationManager2 {
            return instance ?: synchronized(this) {
                instance ?: GeoNotificationManager2(
                    context.applicationContext,
                    firebaseManager,
                    sharedPref
                ).also { instance = it }
            }
        }
    }

    private val geofencingClient: GeofencingClient by lazy {
        LocationServices.getGeofencingClient(context)
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(ACTION_GEOFENCE_TRANSITION).apply {
            setPackage(context.packageName)
        }
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun loadGeoFencesFromFirebase(onComplete: (Boolean) -> Unit = {}) {
        val nickname = sharedPref.getUserName() ?: run {
            onComplete(false)
            return
        }

        firebaseManager.readSingleList(
            "Users/$nickname/Reminders/",
            NotificationData::class.java
        ) { notifications ->
            if (notifications.isNullOrEmpty()) {
                Log.w("GeoManager", "Firebase'dan bo'sh ma'lumot qaytdi")
                onComplete(false)
                return@readSingleList
            }

            val geoFences = notifications.mapNotNull { notification ->
                if (notification.type == 2 && notification.location != null) {
                    createGeofence(
                        notification.id,
                        notification.location!!.latitude,
                        notification.location!!.longitude,
                        notification.location!!.radius ?: GEOFENCE_RADIUS_DEFAULT
                    )
                } else null
            }

            if (geoFences.isNotEmpty()) {
                addGeoFences(geoFences, onComplete)
            } else {
                Log.d("GeoManager", "Geofence'lar topilmadi")
                onComplete(false)
            }
        }
    }

    private fun createGeofence(
        id: String,
        latitude: Double,
        longitude: Double,
        radius: Float
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(latitude, longitude, radius)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(GEOFENCE_EXPIRATION_MS)
            .build()
    }

    private fun addGeoFences(
        geofences: List<Geofence>,
        onComplete: (Boolean) -> Unit = {}
    ) {
        if (!checkLocationPermissions()) {
            Log.e("GeoManager", "Location ruxsatlari yetarli emas")
            onComplete(false)
            return
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        geofencingClient.addGeofences(request, geofencePendingIntent)
            .addOnSuccessListener {
                Log.d("GeoManager", "${geofences.size} ta Geofence muvaffaqiyatli qo'shildi")
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                Log.e("GeoManager", "Geofence qo'shishda xatolik: ${exception.message}")
                onComplete(false)
            }
    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                        ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun clearAllGeofences(onComplete: (Boolean) -> Unit = {}) {
        geofencingClient.removeGeofences(geofencePendingIntent)
            .addOnSuccessListener {
                Log.d("GeoManager", "Barcha Geofence'lar muvaffaqiyatli tozalandi")
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                Log.e("GeoManager", "Geofence'larni tozalashda xatolik: ${exception.message}")
                onComplete(false)
            }
    }
}