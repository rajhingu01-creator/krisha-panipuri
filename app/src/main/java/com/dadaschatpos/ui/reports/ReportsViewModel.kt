package com.dadaschatpos.ui.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dadaschatpos.data.model.ReportFilter
import com.dadaschatpos.data.model.ReportSummary
import com.dadaschatpos.data.repository.OrderRepository
import com.dadaschatpos.util.UiState
import kotlinx.coroutines.launch

class ReportsViewModel(private val orderRepository: OrderRepository) : ViewModel() {
    private val _summaryState = MutableLiveData<UiState<ReportSummary>>(UiState.Idle)
    val summaryState: LiveData<UiState<ReportSummary>> = _summaryState

    fun load(filter: ReportFilter) {
        viewModelScope.launch {
            _summaryState.value = UiState.Loading
            runCatching { orderRepository.report(filter) }
                .onSuccess { _summaryState.value = UiState.Success(it) }
                .onFailure { _summaryState.value = UiState.Error(it.message ?: "Unable to load report") }
        }
    }
}
