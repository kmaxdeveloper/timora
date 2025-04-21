package uz.kmax.timora.presentation.fragment.main.notification

import android.content.pm.PackageManager
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.timora.data.enums.TaskPriority
import uz.kmax.timora.data.helper.NotifyWork
import uz.kmax.timora.data.helper.NotifyWork.Companion.NOTIFICATION_ID
import uz.kmax.timora.data.helper.NotifyWork.Companion.NOTIFICATION_WORK
import uz.kmax.timora.data.managers.DelayNotification
import uz.kmax.timora.data.tools.firebase.FirebaseManager
import uz.kmax.timora.data.tools.tools.GetLocation
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.data.tools.tools.onFragmentBackPressed
import uz.kmax.timora.data.tools.tools.toast
import uz.kmax.timora.databinding.FragmentAddNotificationBinding
import uz.kmax.timora.domain.data.main.LocationData
import uz.kmax.timora.domain.data.main.NotificationData
import uz.kmax.timora.presentation.dialog.SetDateDialog
import uz.kmax.timora.presentation.dialog.SetLocationDialog2
import uz.kmax.timora.presentation.fragment.main.MenuFragment
import java.text.SimpleDateFormat
import java.util.Random
import java.util.concurrent.TimeUnit.MILLISECONDS

class AddNotificationFragment(private var type: Int) :
    BaseFragmentWC<FragmentAddNotificationBinding>(FragmentAddNotificationBinding::inflate) {
    // NotificationData
    private var id = ""
    private var isCompleted = false
    private var latitude = 0.0
    private var longitude = 0.0
    private var radius = 50f
    private var priority = TaskPriority.MEDIUM
    private var createdAt = 0L
    private var isTurned = true
    private var notifyTime = 0L
    // NotificationData

    private var timeDateDialog = SetDateDialog()
    private lateinit var locationDialog: SetLocationDialog2
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var sharedPref: SharedPref
    private var currentLocation = GetLocation()

    override fun onViewCreated() {
        locationDialog = SetLocationDialog2(requireContext())
        firebaseManager = FirebaseManager()
        sharedPref = SharedPref(requireContext())

        locationDialog.setOnLocationClickListener { location ->
            latitude = location.latitude
            longitude = location.longitude
        }

        if (type == 1) {
            binding.locationBtn.visibility = View.GONE
        } else if (type == 2) {
            binding.locationBtn.visibility = View.VISIBLE
        }
        binding.backBtn.setOnClickListener {
            startMainFragment(MenuFragment())
        }

        onFragmentBackPressed {
            startMainFragment(MenuFragment())
        }

        binding.locationBtn.setOnClickListener {
            checkAndRequestLocationPermission()
            currentLocation.getCurrentLocation(requireContext()){currentLocation->
                locationDialog.show(currentLocation)
            }
            toast(requireContext(), "Location Clicked !")
        }

        binding.timeDateBtn.setOnClickListener {
            toast(requireContext(), "Date and Time Clicked")
            timeDateDialog.show(requireContext(), requireActivity()) { timeStamp ->
                notifyTime = timeStamp
                val sdf = SimpleDateFormat("dd/MM/yyy HH:mm")
                toast(requireContext(), sdf.format(notifyTime))
            }
        }

        binding.addBtn.setOnClickListener {
            val notifyTitle = binding.setNewNotify.text.toString()
            val notifyDescription = binding.setNewDescription.text.toString()
            createdAt = System.currentTimeMillis()
            id = "${System.currentTimeMillis()}-${Random().nextInt(99999)}"
            if (notifyTitle.isNotEmpty() &&
                notifyDescription.isNotEmpty()
                && id.isNotEmpty() &&
                createdAt > 0 && notifyTime > 0
            ) {
                if (sharedPref.getUserName() != "") {
                    addNotificationToFirebase(notifyTitle, notifyDescription)
                } else {
                    showSnackBar("UserName kiritilmagan !")
                }
            } else {
                showSnackBar("Biror bir maydon yakunlanmadi !")
            }
        }
    }

    private fun addNotificationToFirebase(title: String, description: String) {
        when (type) {
            1 -> {
                setDataToFirebase(
                    NotificationData(
                        id = id,
                        title, description, dueDate = notifyTime,
                        isCompleted, isTurned, priority, createdAt,
                        type = 1
                    )
                )

                val sdf = SimpleDateFormat("dd/MM/yyy HH:mm")
                val sdFormated = sdf.format(notifyTime)
                val time : Long = notifyTime - System.currentTimeMillis()
                ////////////////////////////////////////////////////////////////////
                val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
                scheduleNotification(time,data)
            }

            2 -> {
                if(latitude != 0.0 && longitude != 0.0) {
                    setDataToFirebase(
                        NotificationData(
                            id = id,
                            title,
                            description,
                            dueDate = notifyTime,
                            isCompleted,
                            isTurned,
                            priority,
                            createdAt,
                            LocationData(latitude, longitude, radius),
                            2
                        )
                    )
                }else{
                    showSnackBar("Manzil kiritilmagan !")
                }
            }
        }
    }

    private fun setDataToFirebase(data: NotificationData) {
        val userName = sharedPref.getUserName()
        firebaseManager.writeData("Users/$userName/Reminders/$id", data) { bool, error ->
            if (bool) {
                showSnackBar("Bilidirshnoma muaffaqiyatli qo'shildi")
                Thread.sleep(2000)
                startMainFragment(MenuFragment())
            } else {
                toast(requireContext(), "Error : $error")
            }
        }
    }

    private fun checkAndRequestLocationPermission() {
        val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(requireContext(), locationPermission) == PackageManager.PERMISSION_GRANTED) {
            // Ruxsat allaqachon berilgan
            toast(requireContext(),"Lokatsiya ruxsati allaqachon berilgan")
        } else {
            // Ruxsat so‘rash
            requestPermissionLauncher.launch(locationPermission)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted) {
            toast(requireContext(),"Lokatsiya ruxsati berildi")
        } else {
            toast(requireContext(),"Lokatsiya ruxsati rad etildi")
        }
    }

    private fun showSnackBar(message : String){
        Snackbar.make(binding.addBtn, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.GREEN)
            .setTextColor(Color.WHITE)
            .show()
    }

    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
            .setInitialDelay(delay, MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(requireContext())
        instanceWorkManager.beginUniqueWork(NOTIFICATION_WORK,
            ExistingWorkPolicy.REPLACE, notificationWork).enqueue()
    }
}