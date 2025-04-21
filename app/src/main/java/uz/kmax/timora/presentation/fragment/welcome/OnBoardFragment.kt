package uz.kmax.timora.presentation.fragment.welcome

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.timora.R
import uz.kmax.timora.data.adapter.OnBoardAdapter
import uz.kmax.timora.data.tools.tools.SharedPref
import uz.kmax.timora.databinding.FragmentOnBoardBinding
import uz.kmax.timora.presentation.fragment.auth.SignInFragment

class OnBoardFragment : BaseFragmentWC<FragmentOnBoardBinding>(FragmentOnBoardBinding::inflate) {
    private lateinit var adapter: OnBoardAdapter
    private lateinit var shared: SharedPref

    override fun onViewCreated() {
        shared = SharedPref(requireContext())
        adapter = OnBoardAdapter(requireContext())
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.indicator, binding.viewPager) { tab, position -> }.attach()
        binding.start.setOnClickListener {
            shared.setWelcomeStatus(resume = false)

            if (!shared.getUserStatus()){
                startMainFragment(SignInFragment())
            }else {
                startMainFragment(SplashFragment())
            }
        }

        binding.nextButton.setOnClickListener {
            binding.viewPager.currentItem += 1
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                when(position){
                    0->{
                        setText(getString(R.string.onboard_1))
                        binding.indicator.visibility = View.VISIBLE
                        binding.start.visibility = View.INVISIBLE
                        binding.nextButton.visibility = View.VISIBLE
                    }
                    1->{
                        setText(getString(R.string.onboard_2))
                        binding.indicator.visibility = View.VISIBLE
                        binding.start.visibility = View.INVISIBLE
                        binding.nextButton.visibility = View.VISIBLE
                    }
                    2->{
                        setText(getString(R.string.onboard_3))
                        binding.indicator.visibility = View.INVISIBLE
                        binding.nextButton.visibility = View.INVISIBLE
                        binding.start.visibility = View.VISIBLE
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    fun setText(text : String){
        binding.welcomeText.text = text
    }
}