package uz.kmax.timora.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.kmax.base.fragmentcontroller.FragmentController
import uz.kmax.timora.R
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.databinding.ActivityMainBinding
import uz.kmax.timora.presentation.fragment.auth.SignInFragment
import uz.kmax.timora.presentation.fragment.welcome.OnBoardFragment
import uz.kmax.timora.presentation.fragment.welcome.SplashFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPref(this)

        FragmentController.init(R.id.container,supportFragmentManager)
        if (sharedPref.getWelcomeStatus()) {
            FragmentController.controller?.startMainFragment(OnBoardFragment())
        }else{
            if (sharedPref.getUserStatus()){
                FragmentController.controller?.startMainFragment(SplashFragment())
            }else{
                FragmentController.controller?.startMainFragment(SignInFragment())
            }
        }
    }
}