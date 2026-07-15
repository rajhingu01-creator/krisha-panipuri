package com.dadaschatpos.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.dadaschatpos.DadasPosApplication
import com.dadaschatpos.R
import com.dadaschatpos.databinding.FragmentOnboardingBinding
import com.dadaschatpos.util.hide
import com.dadaschatpos.util.show
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val pages = listOf(
        OnboardingPage(
            title = "ઝડપી ઓર્ડર અને બિલિંગ",
            description = "પાણીપુરી, ચાટ, ભેળ અને અન્ય ફાસ્ટ ફૂડ માટે સરળ POS સિસ્ટમ",
            imageRes = R.drawable.ic_intro_pos
        ),
        OnboardingPage(
            title = "દૈનિક વેચાણ રિપોર્ટ",
            description = "દરરોજના વેચાણ, બિલ અને પ્રોફિટ ટ્રેક કરો",
            imageRes = R.drawable.ic_intro_reports
        ),
        OnboardingPage(
            title = "બિલ પ્રિન્ટ અને શેર",
            description = "WhatsApp, PDF અને Printer દ્વારા બિલ શેર કરો",
            imageRes = R.drawable.ic_intro_share
        )
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewPager.adapter = OnboardingAdapter(pages)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
        updateButtons(0)

        binding.buttonSkip.setOnClickListener { finishOnboarding() }
        binding.buttonNext.setOnClickListener {
            val next = binding.viewPager.currentItem + 1
            if (next < pages.size) binding.viewPager.currentItem = next else finishOnboarding()
        }
        binding.buttonGetStarted.setOnClickListener { finishOnboarding() }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtons(position)
            }
        })
    }

    private fun updateButtons(position: Int) {
        val lastPage = position == pages.lastIndex
        if (lastPage) {
            binding.buttonNext.hide()
            binding.buttonGetStarted.show()
        } else {
            binding.buttonNext.show()
            binding.buttonGetStarted.hide()
        }
    }

    private fun finishOnboarding() {
        val app = requireActivity().application as DadasPosApplication
        app.sessionManager.completeOnboarding()
        findNavController().navigate(R.id.action_onboarding_to_login)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
