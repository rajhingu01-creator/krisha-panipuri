package com.dadaschatpos.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dadaschatpos.data.repository.AuthRepository
import com.dadaschatpos.data.repository.BackupRepository
import com.dadaschatpos.util.UiState
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {
    private val _backupState = MutableLiveData<UiState<String>>(UiState.Idle)
    val backupState: LiveData<UiState<String>> = _backupState

    fun exportBackup() {
        viewModelScope.launch {
            _backupState.value = UiState.Loading
            runCatching { backupRepository.exportJson() }
                .onSuccess { _backupState.value = UiState.Success(it) }
                .onFailure { _backupState.value = UiState.Error(it.message ?: "Backup export failed") }
        }
    }

    fun importBackup(json: String) {
        viewModelScope.launch {
            _backupState.value = UiState.Loading
            runCatching { backupRepository.importJson(json) }
                .onSuccess { _backupState.value = UiState.Success("Backup restored successfully") }
                .onFailure { _backupState.value = UiState.Error(it.message ?: "Backup restore failed") }
        }
    }

    fun logout() = authRepository.logout()

    fun resetBackupState() {
        _backupState.value = UiState.Idle
    }
}
