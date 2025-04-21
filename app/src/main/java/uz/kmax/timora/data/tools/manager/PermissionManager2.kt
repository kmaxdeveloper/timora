package uz.kmax.timora.data.tools.manager

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionManager2(
    private val fragment: Fragment,
    private val onAllPermissionsGranted: () -> Unit
) {
    private var permissionIndex = 0
    private val permissionsToRequest = mutableListOf<String>()

    fun startRequestingPermissions() {
        permissionsToRequest.clear()
        permissionIndex = 0

        // 1. ACCESS_FINE_LOCATION
        if (!isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 2. ACCESS_BACKGROUND_LOCATION (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !isGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        // 3. POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !isGranted(Manifest.permission.POST_NOTIFICATIONS)
        ) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestNextPermission()
        } else {
            onAllPermissionsGranted()
        }
    }

    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode in 2000..2100) {
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            val currentPermission = permissionsToRequest.getOrNull(permissionIndex)

            if (!granted && currentPermission != null) {
                if (!fragment.shouldShowRequestPermissionRationale(currentPermission)) {
                    showGoToSettingsDialog(currentPermission)
                    return
                }
            }

            permissionIndex++
            if (permissionIndex < permissionsToRequest.size) {
                requestNextPermission()
            } else {
                onAllPermissionsGranted()
            }
        }
    }

    private fun requestNextPermission() {
        if (permissionIndex < permissionsToRequest.size) {
            val permission = permissionsToRequest[permissionIndex]
            fragment.requestPermissions(arrayOf(permission), 2000 + permissionIndex)
        }
    }

    private fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun showGoToSettingsDialog(permission: String) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Ruxsat kerak")
            .setMessage("Ilova $permission ruxsatiga muhtoj. Sozlamalardan ruxsat bering.")
            .setPositiveButton("Sozlamaga o'tish") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", fragment.requireContext().packageName, null)
                fragment.startActivity(intent)
            }
            .setNegativeButton("Bekor qilish", null)
            .show()
    }
}
