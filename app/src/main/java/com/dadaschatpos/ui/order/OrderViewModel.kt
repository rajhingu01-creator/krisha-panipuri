package com.dadaschatpos.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dadaschatpos.data.model.CartItem
import com.dadaschatpos.data.model.ItemEntity
import com.dadaschatpos.data.repository.ItemRepository
import com.dadaschatpos.data.repository.OrderRepository
import com.dadaschatpos.util.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class OrderViewModel(
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val items: LiveData<List<ItemEntity>> = searchQuery
        .flatMapLatest { query -> itemRepository.searchItems(query) }
        .asLiveData()

    private val _cart = MutableLiveData<List<CartItem>>(emptyList())
    val cart: LiveData<List<CartItem>> = _cart

    private val _total = MutableLiveData(0.0)
    val total: LiveData<Double> = _total

    private val _generateState = MutableLiveData<UiState<Long>>(UiState.Idle)
    val generateState: LiveData<UiState<Long>> = _generateState

    fun updateSearch(query: String) {
        searchQuery.value = query
    }

    fun addItem(item: ItemEntity) {
        val current = _cart.value.orEmpty().toMutableList()
        val index = current.indexOfFirst { it.item.id == item.id }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
        } else {
            current.add(CartItem(item, 1))
        }
        updateCart(current)
    }

    fun increase(itemId: Long) {
        updateCart(_cart.value.orEmpty().map { cartItem ->
            if (cartItem.item.id == itemId) cartItem.copy(quantity = cartItem.quantity + 1) else cartItem
        })
    }

    fun decrease(itemId: Long) {
        updateCart(_cart.value.orEmpty().mapNotNull { cartItem ->
            when {
                cartItem.item.id != itemId -> cartItem
                cartItem.quantity > 1 -> cartItem.copy(quantity = cartItem.quantity - 1)
                else -> null
            }
        })
    }

    fun remove(itemId: Long) {
        updateCart(_cart.value.orEmpty().filterNot { it.item.id == itemId })
    }

    fun resetCart() = updateCart(emptyList())

    fun generateBill() {
        val cartItems = _cart.value.orEmpty()
        if (cartItems.isEmpty()) {
            _generateState.value = UiState.Error("Cart is empty")
            return
        }
        viewModelScope.launch {
            _generateState.value = UiState.Loading
            runCatching { orderRepository.createOrder(cartItems) }
                .onSuccess { orderId ->
                    resetCart()
                    _generateState.value = UiState.Success(orderId)
                }
                .onFailure { _generateState.value = UiState.Error(it.message ?: "Unable to generate bill") }
        }
    }

    fun resetGenerateState() {
        _generateState.value = UiState.Idle
    }

    private fun updateCart(cartItems: List<CartItem>) {
        _cart.value = cartItems
        _total.value = cartItems.sumOf { it.amount }
    }
}
