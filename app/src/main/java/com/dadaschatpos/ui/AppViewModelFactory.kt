package com.dadaschatpos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dadaschatpos.DadasPosApplication
import com.dadaschatpos.ui.auth.AuthViewModel
import com.dadaschatpos.ui.bill.BillViewModel
import com.dadaschatpos.ui.order.OrderViewModel
import com.dadaschatpos.ui.reports.ReportsViewModel
import com.dadaschatpos.ui.settings.ItemManagementViewModel
import com.dadaschatpos.ui.settings.SettingsViewModel

class AppViewModelFactory(
    private val app: DadasPosApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(app.authRepository) as T
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> OrderViewModel(app.itemRepository, app.orderRepository) as T
            modelClass.isAssignableFrom(BillViewModel::class.java) -> BillViewModel(app.authRepository, app.orderRepository) as T
            modelClass.isAssignableFrom(ReportsViewModel::class.java) -> ReportsViewModel(app.orderRepository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(app.authRepository, app.backupRepository) as T
            modelClass.isAssignableFrom(ItemManagementViewModel::class.java) -> ItemManagementViewModel(app.itemRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
