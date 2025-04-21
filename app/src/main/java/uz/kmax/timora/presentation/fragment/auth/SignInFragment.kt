package uz.kmax.timora.presentation.fragment.auth

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.timora.R
import uz.kmax.timora.data.tools.auth.MailRemover
import uz.kmax.timora.data.tools.firebase.FirebaseManager
import uz.kmax.timora.data.tools.security.Crypt
import uz.kmax.timora.data.tools.security.Decode
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.data.tools.tools.toast
import uz.kmax.timora.databinding.FragmentSignInBinding
import uz.kmax.timora.domain.data.main.UserData
import uz.kmax.timora.presentation.fragment.welcome.SplashFragment
import java.util.Random

class SignInFragment : BaseFragmentWC<FragmentSignInBinding>(FragmentSignInBinding::inflate) {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPref: SharedPref
    private lateinit var firebaseManager: FirebaseManager

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-in failed: ${e.statusCode}")
            }
        }

    override fun onViewCreated() {
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), getGoogleSignInOptions())
        sharedPref = SharedPref(requireContext())
        firebaseManager = FirebaseManager()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  // Oq rangdagi ikonkalarga o'tish

        val context = requireContext()
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        binding.logInBtn.setOnClickListener {
            val nickName = binding.logIn.text.toString()
            val password = binding.password.text.toString()
            val rePassword = binding.rePassword.text.toString()
            if (nickName.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty()) {
                val id = "${System.currentTimeMillis()}-${Random().nextInt(99999)}"
                val created = System.currentTimeMillis()
                val loginType = 1
                if (password == rePassword) {
                    if (password.length >= 5 && nickName.length >= 5){
                        val salt: ByteArray = Crypt.generateSalt()
                        val hashedPassword: String = Crypt.hashPassword(password, salt)
                        val saltPassword: String = Decode.encodeSalt(salt)
                        firebaseManager.checkExist("/Users/$nickName") { status ->
                            toast(context, "Status : $status")
                            if (!status) {
                                setUserDataToFirebase(
                                    UserData(
                                        id = id,
                                        userName = nickName,
                                        passwordSalt = saltPassword,
                                        password = hashedPassword,
                                        createdAt = created,
                                        loginType = loginType
                                    )
                                )
                            } else {
                                showSnackBar("Siz kiritgan Login mavjud !")
                            }
                        }
                    }else if (nickName.length < 5){
                        showSnackBar("UserName 4 belgidan oshiq bo'lishi kerak !")
                    }else{
                        showSnackBar("Parol 4 belgidan oshiq bo'lishi kerak !")
                    }
                } else {
                    showSnackBar("Siz kiritgan parollar mos emas !")
                }
            } else {
                showSnackBar("Barcha maydonlarni to'ldiring !")
            }
        }

        binding.toLogInBtn.setOnClickListener {
            startMainFragment(LogInFragment())
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                var mail = MailRemover.remove(user?.email.toString())
                sharedPref.setUserStatus(true)
                sharedPref.setUserName(mail)
                setUserGoogleDataToFirebase(mail, UserData())
                Toast.makeText(requireContext(), "Welcome ${user?.displayName} !", Toast.LENGTH_SHORT)
                    .show()
                startMainFragment(SplashFragment())
            } else {
                Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Firebase Console dan olib qo‘y
            .requestEmail()
            .build()
    }

    private fun setUserGoogleDataToFirebase(location: String, data: UserData) {
        firebaseManager.writeData("Users/$location", data) { bool, error ->
            if (bool) {
                val mail = MailRemover.remove(auth.currentUser?.email.toString())
                sharedPref.setUserStatus(true)
                sharedPref.setUserName(mail)
                startMainFragment(SplashFragment())
            } else {
                toast(requireContext(), "Error : $error")
            }
        }
    }

    private fun setUserDataToFirebase(data: UserData) {
        firebaseManager.writeData("Users/${data.userName}/", data) { bool, error ->
            if (bool) {
                sharedPref.setUserName(data.userName)
                sharedPref.setUserStatus(true)
                startMainFragment(SplashFragment())
            } else {
                toast(requireContext(), "Error : $error")
            }
        }
    }

    private fun showSnackBar(message : String){
        Snackbar.make(
            binding.logInBtn,
            message,
            Snackbar.LENGTH_SHORT
        )
            .setBackgroundTint(Color.GREEN)
            .setTextColor(Color.WHITE)
            .show()
    }
}
