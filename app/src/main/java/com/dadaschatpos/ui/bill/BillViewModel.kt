package com.dadaschatpos.ui.bill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dadaschatpos.data.model.OrderWithItems
import com.dadaschatpos.data.model.UserEntity
import com.dadaschatpos.data.repository.AuthRepository
import com.dadaschatpos.data.repository.OrderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class BillViewModel(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val selectedOrderId = MutableStateFlow(-1L)

    @OptIn(ExperimentalCoroutinesApi::class)
    val receipt: LiveData<OrderWithItems?> = selectedOrderId
        .flatMapLatest { orderId -> orderRepository.observeOrder(orderId) }
        .asLiveData()

    private val _user = MutableLiveData<UserEntity?>()
    val user: LiveData<UserEntity?> = _user

    fun load(orderId: Long) {
        selectedOrderId.value = orderId
        viewModelScope.launch {
            _user.value = authRepository.currentUser()
        }
    }
}
