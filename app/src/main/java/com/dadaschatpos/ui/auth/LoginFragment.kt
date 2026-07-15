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
import com.dadaschatpos.databinding.FragmentLoginBinding
import com.dadaschatpos.ui.AppViewModelFactory
import com.dadaschatpos.util.UiState
import com.dadaschatpos.util.hide
import com.dadaschatpos.util.show
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AppViewModelFactory(requireActivity().application as DadasPosApplication)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.loginButton.setOnClickListener {
            clearErrors()
            viewModel.login(
                email = binding.emailEditText.text?.toString().orEmpty(),
                password = binding.passwordEditText.text?.toString().orEmpty(),
                remember = binding.rememberCheckBox.isChecked
            )
        }
        binding.registerText.setOnClickListener { findNavController().navigate(R.id.action_login_to_register) }
        binding.forgotPasswordText.setOnClickListener {
            Toast.makeText(requireContext(), "Please contact shop admin to reset password", Toast.LENGTH_SHORT).show()
        }

        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle -> setLoading(false)
                UiState.Loading -> setLoading(true)
                is UiState.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                }
                is UiState.Success -> {
                    setLoading(false)
                    findNavController().navigate(R.id.action_login_to_order)
                }
            }
        }
    }

    private fun clearErrors() {
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
    }

    private fun setLoading(loading: Boolean) {
        binding.loginButton.isEnabled = !loading
        if (loading) binding.loadingIndicator.show() else binding.loadingIndicator.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
