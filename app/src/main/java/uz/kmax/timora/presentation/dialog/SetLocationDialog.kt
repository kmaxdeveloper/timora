package uz.kmax.timora.presentation.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import uz.kmax.timora.R

class SetLocationDialog(
    private val context: Context,
    private val mapFragment: SupportMapFragment
) : OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var selectedLocation: LatLng? = null

    private var locationClickListener : ((LatLng)-> Unit)? = null
    fun setOnLocationClickListener(f: (LatLng)-> Unit){ locationClickListener = f }

    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_set_location, null)
        val btnConfirmLocation = view.findViewById<Button>(R.id.btnConfirmLocation)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Joylashuvni tanlang")
            .setView(view)
            .setNegativeButton("Bekor qilish", null)
            .create()

        mapFragment.getMapAsync(this)

        btnConfirmLocation.setOnClickListener {
            selectedLocation?.let { location ->
                locationClickListener?.invoke(location)
                dialog.dismiss()
            } ?: Toast.makeText(context, "Iltimos, joylashuvni tanlang!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val defaultLocation = LatLng(41.2995, 69.2401) // Toshkent koordinatalari
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Tanlangan joy"))
            selectedLocation = latLng
        }
    }

    private fun saveLocationToFirebase(location: LatLng) {
        val database = FirebaseDatabase.getInstance().reference.child("locations")
        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis()
        )
        database.push().setValue(locationData)
            .addOnSuccessListener {
                Toast.makeText(context, "Joylashuv saqlandi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Xatolik: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
