package uz.kmax.timora.presentation.fragment.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.timora.data.adapter.NotificationListAdapter
import uz.kmax.timora.data.enums.TaskPriority
import uz.kmax.timora.data.geo.GeoNotificationManager
import uz.kmax.timora.data.tools.firebase.FirebaseManager
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.data.tools.tools.toast
import uz.kmax.timora.databinding.FragmentHomeBinding
import uz.kmax.timora.domain.data.main.NotificationData
import uz.kmax.timora.presentation.dialog.NotificationTypeDialog
import uz.kmax.timora.presentation.dialog.work.DeleteNotification
import uz.kmax.timora.presentation.fragment.main.notification.AddNotificationFragment

class HomeFragment : BaseFragmentWC<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val adapter by lazy { NotificationListAdapter() }
    private var dialog = NotificationTypeDialog()
    private var dialogDeleteNotify = DeleteNotification()
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var sharedPref: SharedPref

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        sharedPref = SharedPref(requireContext())

        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter

        loadDataFromFirebase()

        dialog.setOnTypeListener {
            startMainFragment(AddNotificationFragment(it))
        }

        binding.add.setOnClickListener {
            dialog.show(requireContext())
        }

        adapter.setOnTaskListener { task, message ->
            when(task){
                1->{
                    Toast.makeText(requireContext(), "$message clicked !", Toast.LENGTH_SHORT).show()
                }
                2->{
                    Toast.makeText(requireContext(), "$message clicked !", Toast.LENGTH_SHORT).show()
                }
                3->{
                    // Delete
                    dialogDeleteNotify.show(requireContext())
                    dialogDeleteNotify.setDeleteNotifyCloseListener {
                        removeDataFromFirebase(message)
                    }
                    Toast.makeText(requireContext(), "$message clicked !", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun removeDataFromFirebase(id : String){
        val nickname = sharedPref.getUserName().toString()
        firebaseManager.deleteData("Users/$nickname/Reminders/$id"){bool: Boolean, error: String? ->
            if (bool){
                showSnackBar("Notification muaffaqiyatli o'chirildi !")
            }else{
                toast(requireContext(),error.toString())
            }
        }
    }

    private fun loadDataFromFirebase() {
//        val list = ArrayList<NotificationData>()
//        //list.add(NotificationData("2005","First Love","Hello World",12345678910,false,isTurned = false,TaskPriority.MEDIUM,0))
//        adapter.setItems(list)
        val nickname = sharedPref.getUserName().toString()

        firebaseManager.observeList("Users/$nickname/Reminders/",NotificationData::class.java){
            if (it != null){
                adapter.setItems(it)
                if (it.size != 0){
                    binding.emptyList.visibility = View.INVISIBLE
                }else{
                    binding.emptyList.visibility = View.VISIBLE
                }
            }else{
                toast(requireContext(),"Ma'lumot olishda xatolik ro'y berdi !")
            }
        }
    }

    private fun showSnackBar(message : String){
        Snackbar.make(binding.recycleView, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.BLUE)
            .setTextColor(Color.WHITE)
            .show()
    }
}