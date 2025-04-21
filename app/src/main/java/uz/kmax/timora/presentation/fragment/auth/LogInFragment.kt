package uz.kmax.timora.presentation.fragment.auth

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.timora.R
import uz.kmax.timora.data.tools.firebase.FirebaseManager
import uz.kmax.timora.data.tools.security.Crypt
import uz.kmax.timora.data.tools.security.Decode
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.data.tools.tools.toast
import uz.kmax.timora.databinding.FragmentLogInBinding
import uz.kmax.timora.domain.data.main.UserData
import uz.kmax.timora.presentation.fragment.welcome.SplashFragment
import kotlin.math.log

class LogInFragment : BaseFragmentWC<FragmentLogInBinding>(FragmentLogInBinding::inflate) {
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

        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        binding.logInBtn.setOnClickListener {
            val logIn = binding.logIn.text.toString()
            val password = binding.password.text.toString()
            if (logIn.isNotEmpty() && password.isNotEmpty()){
                logIn(logIn, password)
            }else{
                showSnackBar("Barcha qatorlarni to'ldiring !")
            }
        }

        binding.signInBtn.setOnClickListener {
            startMainFragment(SignInFragment())
        }
    }

    private fun logIn(logIn : String , password : String){
        firebaseManager.checkExist("Users/$logIn") {
            if (it) {
                firebaseManager.readData("Users/$logIn", UserData::class.java) { data, error ->
                    if (data != null) {
                        val salt : ByteArray = Decode.getSaltAsByteArray(data.passwordSalt)
                        val hashPassword : String = Crypt.hashPassword(password,salt)
                        if (data.password == hashPassword) {
                            sharedPref.setUserStatus(true)
                            sharedPref.setUserName(data.userName)
                            toast(requireContext(), "Hush Kelibsiz ! ${data.userName}")
                            startMainFragment(SplashFragment())
                        } else {
                            showSnackBar("Siz kiritgan Password noto'g'ri !")
                        }
                    } else {
                        toast(requireContext(), "Error : $error")
                    }
                }
            } else {
                showSnackBar("Siz kiritgan Login mavjud emas!")
            }
        }
    }

    private fun showSnackBar(message : String){
        Snackbar.make(binding.logInBtn, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.GREEN)
            .setTextColor(Color.WHITE)
            .show()
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
                firebaseManager.checkExist("Users/${user?.email.toString()}") {status->
                    if (status) {
                        sharedPref.setUserStatus(true)
                        sharedPref.setUserName(auth.currentUser?.email.toString())
                        Toast.makeText(
                            requireContext(),
                            "Welcome ${user?.displayName}",
                            Toast.LENGTH_SHORT
                        ).show()
                        startMainFragment(SplashFragment())
                    } else {
                        setUserToFirebase(
                            user?.email.toString(), UserData(
                                auth.currentUser?.uid.toString(),
                                user?.email.toString(),
                                "", "",
                                user?.displayName.toString(),
                                user?.email.toString(),
                                user?.photoUrl.toString(),
                                System.currentTimeMillis(), 2
                            )
                        )
                    }
                }
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

    private fun setUserToFirebase(location : String,data : UserData){
        firebaseManager.writeData(location,data){bool,error->
            if (bool){
                sharedPref.setUserStatus(true)
                sharedPref.setUserName(data.email)
                Toast.makeText(requireContext(), "Welcome ${data.name}", Toast.LENGTH_SHORT).show()
                startMainFragment(SplashFragment())
            }else{
                toast(requireContext(),"Xatolik yuz berdi ! Error : $error")
            }
        }
    }
}