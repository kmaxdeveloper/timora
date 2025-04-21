package uz.kmax.timora.data.tools.manager

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import uz.kmax.timora.data.tools.firebase.FirebaseManager
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.domain.data.main.NotificationData

class GeoNotificationManager(private val context: Context) {

    private val geofencingClient = LocationServices.getGeofencingClient(context)
    private val firebaseManager = FirebaseManager()
    private val sharedPref = SharedPref(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent("com.example.GEOFENCE_TRANSITION_ACTION").setPackage(context.packageName)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun loadGeoFencesFromFirebase() {
        val nickname = sharedPref.getUserName()
        firebaseManager.readSingleList("Users/$nickname/Reminders/",NotificationData::class.java){
            if (it != null){
                val geoFences = mutableListOf<Geofence>()
                it.forEach {item->
                    if (item.type == 2){
                        val lat = item.location!!.latitude
                        val lng = item.location!!.longitude
                        val radius = item.location!!.radius
                        val id = item.id

                        val geofence = Geofence.Builder()
                            .setRequestId(id)
                            .setCircularRegion(lat, lng, radius)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .build()

                        geoFences.add(geofence)
                        Toast.makeText(context, "ma'lumotlar keldi !", Toast.LENGTH_SHORT).show()
                    }
                }
                addGeoFences(geoFences)
            }else{
                Toast.makeText(context, "Geo ma'lumotlarni olishda xatolik yuz berdi !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addGeoFences(geoFences: List<Geofence>) {
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geoFences)
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("GeoManager", "Permission yetarli emas")
            return
        }

        geofencingClient.addGeofences(request, geofencePendingIntent)
            .addOnSuccessListener { Log.d("GeoManager", "Geofence’lar qo‘shildi") }
            .addOnFailureListener { Log.e("GeoManager", "Geofence qo‘shishda xatolik: ${it.message}") }
    }

    fun clearAllGeofences() {
        geofencingClient.removeGeofences(geofencePendingIntent)
            .addOnSuccessListener { Log.d("GeoManager", "Geofence’lar tozalandi") }
            .addOnFailureListener { Log.e("GeoManager", "Geofence tozalanmadi: ${it.message}") }
    }
}
