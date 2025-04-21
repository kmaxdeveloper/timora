package uz.kmax.timora.data.tools.manager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionManager(
    private val fragment: Fragment,
    private val onPermissionsGranted: () -> Unit,
    private val onLocationGranted: () -> Unit,
    private val onBackgroundLocationGranted: () -> Unit,
    private val onNotificationGranted: () -> Unit
) {

    companion object {
        const val REQ_LOCATION = 1001
        const val REQ_BACKGROUND = 1002
        const val REQ_NOTIFICATIONS = 1003
    }

    private var permissionsToRequest = mutableListOf<String>()
    private var currentPermissionIndex = 0

    // Launchers
    private val locationPermissionLauncher: ActivityResultLauncher<String>
    private val backgroundLocationPermissionLauncher: ActivityResultLauncher<String>
    private val notificationPermissionLauncher: ActivityResultLauncher<String>

    init {
        // Location permission launcher
        locationPermissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    onLocationGranted()
                    checkNextPermission()
                } else {
                    toast("📍 Joylashuv ruxsati rad etildi")
                }
            }

        // Background Location permission launcher
        backgroundLocationPermissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    onBackgroundLocationGranted()
                    checkNextPermission()
                } else {
                    toast("🕒 Fon joylashuv ruxsati rad etildi")
                }
            }

        // Notification permission launcher
        notificationPermissionLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    onNotificationGranted()
                    checkNextPermission()
                } else {
                    toast("🔔 Bildirishnoma ruxsati rad etildi")
                }
            }
    }

    private fun toast(msg: String) {
        Toast.makeText(fragment.requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    // Permissionni ketma-ket so‘rash
    fun requestPermissionsInSequence() {
        permissionsToRequest.clear()

        // Agar location permission kerak bo‘lsa
        if (!hasLocationPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Agar background location permission kerak bo‘lsa
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasBackgroundLocationPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        // Agar notification permission kerak bo‘lsa
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission()) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Birinchi permissionni so‘rash
        if (permissionsToRequest.isNotEmpty()) {
            requestNextPermission()
        } else {
            onPermissionsGranted()
        }
    }

    // Keyingi permissionni so‘rashi
    private fun requestNextPermission() {
        if (permissionsToRequest.isNotEmpty()) {
            val permission = permissionsToRequest[currentPermissionIndex]
            when (permission) {
                Manifest.permission.ACCESS_FINE_LOCATION -> locationPermissionLauncher.launch(permission)
                Manifest.permission.ACCESS_BACKGROUND_LOCATION -> backgroundLocationPermissionLauncher.launch(permission)
                Manifest.permission.POST_NOTIFICATIONS -> notificationPermissionLauncher.launch(permission)
            }
        }
    }

    // Ruxsat berilganda keyingisini so‘rashing
    private fun checkNextPermission() {
        currentPermissionIndex++
        if (currentPermissionIndex < permissionsToRequest.size) {
            requestNextPermission()
        } else {
            onPermissionsGranted()
        }
    }

    // Location permission mavjudmi?
    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    // Background location permission mavjudmi?
    private fun hasBackgroundLocationPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    // Notification permission mavjudmi?
    private fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
}
