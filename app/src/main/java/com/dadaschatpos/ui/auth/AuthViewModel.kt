package com.dadaschatpos.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dadaschatpos.data.model.UserEntity
import com.dadaschatpos.data.repository.AuthRepository
import com.dadaschatpos.util.UiState
import com.dadaschatpos.util.Validators
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _loginState = MutableLiveData<UiState<UserEntity>>(UiState.Idle)
    val loginState: LiveData<UiState<UserEntity>> = _loginState

    private val _registerState = MutableLiveData<UiState<UserEntity>>(UiState.Idle)
    val registerState: LiveData<UiState<UserEntity>> = _registerState

    fun login(email: String, password: String, remember: Boolean) {
        when {
            !Validators.isEmail(email) -> _loginState.value = UiState.Error("Enter a valid email")
            !Validators.isRequired(password) -> _loginState.value = UiState.Error("Enter password")
            else -> viewModelScope.launch {
                _loginState.value = UiState.Loading
                authRepository.login(email, password, remember)
                    .onSuccess { _loginState.value = UiState.Success(it) }
                    .onFailure { _loginState.value = UiState.Error(it.message ?: "Login failed") }
            }
        }
    }

    fun register(
        fullName: String,
        shopName: String,
        mobile: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        when {
            !Validators.isRequired(fullName) -> _registerState.value = UiState.Error("Enter full name")
            !Validators.isRequired(shopName) -> _registerState.value = UiState.Error("Enter shop name")
            !Validators.isMobile(mobile) -> _registerState.value = UiState.Error("Enter a valid 10 digit mobile number")
            !Validators.isEmail(email) -> _registerState.value = UiState.Error("Enter a valid email")
            !Validators.isPassword(password) -> _registerState.value = UiState.Error("Password must be at least 6 characters")
            password != confirmPassword -> _registerState.value = UiState.Error("Passwords do not match")
            else -> viewModelScope.launch {
                _registerState.value = UiState.Loading
                authRepository.register(fullName, shopName, mobile, email, password)
                    .onSuccess { _registerState.value = UiState.Success(it) }
                    .onFailure { _registerState.value = UiState.Error(it.message ?: "Registration failed") }
            }
        }
    }
}
