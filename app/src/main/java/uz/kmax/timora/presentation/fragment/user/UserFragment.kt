package uz.kmax.timora.presentation.fragment.user

import android.icu.text.SimpleDateFormat
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.timora.R
import uz.kmax.timora.data.tools.firebase.FirebaseManager
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.data.tools.tools.toast
import uz.kmax.timora.databinding.FragmentUserBinding
import uz.kmax.timora.domain.data.main.UserData
import uz.kmax.timora.presentation.dialog.tool.DeleteAccountDialog
import uz.kmax.timora.presentation.dialog.tool.LogOutDialog
import uz.kmax.timora.presentation.fragment.auth.LogInFragment
import java.util.Date
import java.util.Locale

class UserFragment : BaseFragmentWC<FragmentUserBinding>(FragmentUserBinding::inflate) {
    private lateinit var sharedPref: SharedPref
    private lateinit var firebaseManager: FirebaseManager
    private var userName : String = ""
    private var dialog = LogOutDialog()
    private var deleteDialog = DeleteAccountDialog()

    override fun onViewCreated() {
        sharedPref = SharedPref(requireContext())
        firebaseManager = FirebaseManager()
        userName = sharedPref.getUserName().toString()
        getUserDataFromFirebase()

        dialog.setLogOutCloseListener {
            sharedPref.setUserStatus(false)
            sharedPref.setUserName("")
            startMainFragment(LogInFragment())
        }

        deleteDialog.setDeleteCloseListener {
            firebaseManager.deleteData("Users/$userName"){ bool, error->
                if (bool){
                    toast(requireContext(),"Akkount muaffaqiyatli o'chirldi !")
                    sharedPref.setUserStatus(false)
                    sharedPref.setUserName("")
                    startMainFragment(LogInFragment())
                }else{
                    toast(requireContext(),"Error : $error")
                }
            }
        }

        binding.logOut.setOnClickListener {
            dialog.show(requireContext())
        }

        binding.delete.setOnClickListener {
            deleteDialog.show(requireContext())
        }
    }

    private fun getUserDataFromFirebase() {
        firebaseManager.readData("Users/$userName",UserData::class.java){ data, error->
            if (data != null){
                bindDataToView(data)
            }else{
                toast(requireContext(),"Error : $error")
            }
        }
    }

    private fun bindDataToView(data: UserData) {
        val timestamp = data.createdAt
        val date: Date = Date(timestamp) // Longni Date formatiga aylantirish
        val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate: String = sdf.format(date) // Formatlangan sana va vaqt

        binding.userID.text = data.id
        binding.userName.text = data.userName
        binding.userCreatedAt.text = formattedDate

        when(data.gender){
            0->{
                binding.userImage.setImageResource(R.drawable.image_user)
            }
            1->{
                binding.userImage.setImageResource(R.drawable.image_user_male)
            }
            2->{
                binding.userImage.setImageResource(R.drawable.image_user_female)
            }
        }
    }
}