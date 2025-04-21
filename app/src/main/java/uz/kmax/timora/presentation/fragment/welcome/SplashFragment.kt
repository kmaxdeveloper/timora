package uz.kmax.timora.presentation.fragment.welcome

import android.os.CountDownTimer
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.timora.data.tools.tools.ConnectionManager
import uz.kmax.timora.databinding.FragmentSplashBinding
import uz.kmax.timora.presentation.fragment.main.MenuFragment

class SplashFragment : BaseFragmentWC<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    override fun onViewCreated() {
        object : CountDownTimer(3000, 100) {
            override fun onFinish() {
                if (ConnectionManager().check(requireContext())) {
                    startMainFragment(MenuFragment())
                } else {
                    if (ConnectionManager().check(requireContext())) {
                        startMainFragment(MenuFragment())
                    } else {
                        //s
                    }
                }
            }

            override fun onTick(value: Long) {}
        }.start()
    }
}