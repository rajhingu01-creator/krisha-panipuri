package com.dadaschatpos.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dadaschatpos.data.model.ItemEntity
import com.dadaschatpos.data.repository.ItemRepository
import com.dadaschatpos.util.UiState
import kotlinx.coroutines.launch

class ItemManagementViewModel(private val itemRepository: ItemRepository) : ViewModel() {
    val items: LiveData<List<ItemEntity>> = itemRepository.observeItems().asLiveData()

    private val _state = MutableLiveData<UiState<String>>(UiState.Idle)
    val state: LiveData<UiState<String>> = _state

    private var editingItem: ItemEntity? = null

    fun startEditing(item: ItemEntity) {
        editingItem = item
    }

    fun clearEditing() {
        editingItem = null
    }

    fun save(name: String, price: String, category: String, image: String?) {
        val priceValue = price.toDoubleOrNull()
        when {
            name.trim().isEmpty() -> _state.value = UiState.Error("Enter item name")
            priceValue == null || priceValue <= 0.0 -> _state.value = UiState.Error("Enter valid price")
            category.trim().isEmpty() -> _state.value = UiState.Error("Enter category")
            else -> viewModelScope.launch {
                _state.value = UiState.Loading
                val current = editingItem
                val item = if (current == null) {
                    ItemEntity(
                        name = name.trim(),
                        price = priceValue,
                        category = category.trim(),
                        image = image ?: "ic_food_panipuri"
                    )
                } else {
                    current.copy(
                        name = name.trim(),
                        price = priceValue,
                        category = category.trim(),
                        image = image ?: current.image
                    )
                }
                runCatching { itemRepository.save(item) }
                    .onSuccess {
                        editingItem = null
                        _state.value = UiState.Success("Item saved")
                    }
                    .onFailure { _state.value = UiState.Error(it.message ?: "Unable to save item") }
            }
        }
    }

    fun delete(item: ItemEntity) {
        viewModelScope.launch {
            runCatching { itemRepository.delete(item) }
                .onSuccess { _state.value = UiState.Success("Item deleted") }
                .onFailure { _state.value = UiState.Error(it.message ?: "Unable to delete item") }
        }
    }

    fun resetState() {
        _state.value = UiState.Idle
    }
}
