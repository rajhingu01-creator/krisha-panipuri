package com.dadaschatpos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dadaschatpos.DadasPosApplication
import com.dadaschatpos.R
import com.dadaschatpos.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            val app = requireActivity().application as DadasPosApplication
            val destination = when {
                app.sessionManager.isRememberedLogin -> R.id.action_splash_to_order
                !app.sessionManager.isOnboardingComplete -> R.id.action_splash_to_onboarding
                else -> R.id.action_splash_to_login
            }
            findNavController().navigate(destination)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
