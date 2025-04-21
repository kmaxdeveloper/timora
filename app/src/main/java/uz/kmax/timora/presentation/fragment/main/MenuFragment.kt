package uz.kmax.timora.presentation.fragment.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.base.fragmentcontroller.InnerFragmentController
import uz.kmax.timora.R
import uz.kmax.timora.data.geo.GeoNotificationManager
import uz.kmax.timora.data.geo.GeoNotificationManager2
import uz.kmax.timora.data.geo.GeoNotificationManager3
import uz.kmax.timora.data.tools.firebase.FirebaseManager
import uz.kmax.timora.data.tools.manager.PermissionManager
import uz.kmax.timora.data.tools.manager.PermissionManager2
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.data.tools.tools.toast
import uz.kmax.timora.databinding.FragmentMenuBinding
import uz.kmax.timora.domain.data.main.NotificationData
import uz.kmax.timora.presentation.fragment.tool.AdminFragment
import uz.kmax.timora.presentation.fragment.tool.PrivacyFragment
import uz.kmax.timora.presentation.fragment.tool.SettingsFragment
import uz.kmax.timora.presentation.fragment.user.UserFragment

class MenuFragment : BaseFragmentWC<FragmentMenuBinding>(FragmentMenuBinding::inflate) {
    private lateinit var toggleBar: ActionBarDrawerToggle
    private lateinit var firebaseManager: FirebaseManager
    private var geoNotificationManager = GeoNotificationManager()
    private lateinit var shared: SharedPref
    private lateinit var permissionManager: PermissionManager2

    override fun onViewCreated() {
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.appTheme)

        firebaseManager = FirebaseManager()
        shared = SharedPref(requireContext())

        permissionManager = PermissionManager2(this) {
            toast("all permissions is granted")
            addGeofence(requireContext())
        }

        permissionManager.startRequestingPermissions()

        InnerFragmentController.init(R.id.innerContainer, requireActivity().supportFragmentManager)
        replaceInnerFragment(HomeFragment())

        toggleBar = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggleBar)
        toggleBar.syncState()

        binding.bottomNavigation.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.appTheme
            )
        )
        binding.bottomNavigation.itemIconTintList = null

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    // Respond to navigation item 1 click
                    replaceInnerFragment(HomeFragment())
                    true
                }

                R.id.action_to_do_List -> {
                    // Respond to navigation item 2 click
                    replaceInnerFragment(ToDoListFragment())
                    true
                }

                R.id.action_account -> {
                    replaceInnerFragment(UserFragment())
                    true
                }

                R.id.action_settings -> {
                    // Sozlamalar fragmentiga o'tish
                    replaceInnerFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }

        binding.navigationMenu.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.homePage -> {
                    replaceInnerFragment(HomeFragment())
                    closeDrawer()
                    binding.drawerLayout.isSelected = false
                }

                R.id.ratingApp -> {
//                    val manager = ReviewManagerFactory.create(requireContext())
//                    val request = manager.requestReviewFlow()
//                    request.addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            val reviewInfo = task.result
//                            val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
//                            flow.addOnCompleteListener { result ->
//                                if (result.isCanceled) {
//                                    toast("Dasturni baholash bekor qilindi !")
//                                } else if (result.isSuccessful) {
//                                    toast("Dastur baholandi !!!")
//                                } else if (result.isComplete) {
//                                    toast("Baholash tugatildi !")
//                                }
//                            }
//                        } else {
//                            @ReviewErrorCode val reviewErrorCode =
//                                (task.exception as ReviewException).errorCode
//                        }
//                    }
                    toast("Rating clicked !")
                    closeDrawer()
                    binding.drawerLayout.isSelected = false
                }

                R.id.devConnection -> {
                    replaceInnerFragment(AdminFragment())
                    closeDrawer()
                }

                R.id.privacyPolicy -> {
                    replaceInnerFragment(PrivacyFragment())
                    closeDrawer()
                }

                else -> return@OnNavigationItemSelectedListener true
            }
            true
        })

    }

    private fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START, true)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun replaceInnerFragment(fragment: Fragment) {
        InnerFragmentController.innerController?.startInnerMainFragment(fragment)
    }

    private fun addGeofence(context: Context) {
        val nickname = shared.getUserName()
        firebaseManager.readSingleList(
            "Users/$nickname/Reminders/",
            NotificationData::class.java
        ) { item ->
            if (item != null) {
                GeoNotificationManager3.removeAllGeofences(context) { status ->
                    if (status) {
                        item.forEach { itemList ->
                            if (itemList.type == 2) {
                                GeoNotificationManager3.safelyAddGeoFence(
                                    context = context,
                                    latitude = itemList.location!!.latitude,
                                    longitude = itemList.location!!.longitude
                                ) { success ->
                                    if (success) {
                                        //toast("📍 Geozona faol holatda!")
                                    } else {
                                        //toast("⚠️ Geozona ishlamadi yoki ruxsat yo‘q")
                                    }
                                }
                            }
                        }
                    } else {
                        toast("Geo Fence o'chirishda xatolik kelib chiqdi !")
                    }
                }
            } else {
                toast(context, "List is empty !")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.handlePermissionsResult(requestCode, permissions, grantResults)
    }
}