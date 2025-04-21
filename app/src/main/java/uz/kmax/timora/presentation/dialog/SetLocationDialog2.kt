package uz.kmax.timora.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import uz.kmax.timora.databinding.DialogSetLocation2Binding

class SetLocationDialog2(
    private val context: Context
) {

    private var selectedLatLng: LatLng? = null
    private var locationClickListener : ((LatLng)-> Unit)? = null
    fun setOnLocationClickListener(f: (LatLng)-> Unit){ locationClickListener = f }

    fun show(latLng : LatLng){
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogSetLocation2Binding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.mapView.onCreate(null)
        binding.mapView.getMapAsync { map ->
            val googleMap: GoogleMap = map
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true

            // Default joy (Toshkent)
            var defaultLocation = latLng ?: LatLng(41.2995, 69.2401)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

            val markerOptions = MarkerOptions().position(defaultLocation).title("Tanlangan joy")
            val marker = googleMap.addMarker(markerOptions)

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,10f))

            // Xarita bosilganda markerni yangilash
            googleMap.setOnMapClickListener { latLng ->
                selectedLatLng = latLng
                marker?.position = latLng
            }
        }

        binding.btnConfirmLocation.setOnClickListener {
            selectedLatLng?.let { locationClickListener?.invoke(it) }
            dialog.dismiss()
        }

        dialog.show()
    }
}