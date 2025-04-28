package com.charchil.reminderpro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charchil.reminderpro.domain.model.Reminder
import com.charchil.reminderpro.domain.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val data: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.getAllReminders().collectLatest { reminders ->
                    _uiState.value = _uiState.value.copy(
                        data = reminders,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    fun insert(reminder: Reminder) {
        viewModelScope.launch {
            try {
                repository.insert(reminder)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to insert reminder"
                )
            }
        }
    }

    fun update(reminder: Reminder) {
        viewModelScope.launch {
            try {
                repository.update(reminder)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to update reminder"
                )
            }
        }
    }

    fun delete(reminder: Reminder) {
        viewModelScope.launch {
            try {
                repository.delete(reminder)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete reminder"
                )
            }
        }
    }
} 