package com.dadaschatpos.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dadaschatpos.DadasPosApplication
import com.dadaschatpos.R
import com.dadaschatpos.databinding.FragmentRegisterBinding
import com.dadaschatpos.ui.AppViewModelFactory
import com.dadaschatpos.util.UiState
import com.dadaschatpos.util.hide
import com.dadaschatpos.util.show
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as DadasPosApplication)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.registerButton.setOnClickListener {
            viewModel.register(
                fullName = binding.fullNameEditText.text?.toString().orEmpty(),
                shopName = binding.shopNameEditText.text?.toString().orEmpty(),
                mobile = binding.mobileEditText.text?.toString().orEmpty(),
                email = binding.emailEditText.text?.toString().orEmpty(),
                password = binding.passwordEditText.text?.toString().orEmpty(),
                confirmPassword = binding.confirmPasswordEditText.text?.toString().orEmpty()
            )
        }

        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle -> setLoading(false)
                UiState.Loading -> setLoading(true)
                is UiState.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                }
                is UiState.Success -> {
                    setLoading(false)
                    Toast.makeText(requireContext(), "Registration successful. Please login.", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_register_to_login)
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.registerButton.isEnabled = !loading
        if (loading) binding.loadingIndicator.show() else binding.loadingIndicator.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
