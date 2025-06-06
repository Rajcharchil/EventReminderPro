package com.charchil.reminderpro.presentation.ui

import androidx.lifecycle.ViewModel
import com.charchil.reminderpro.domain.model.Reminder
import com.charchil.reminderpro.domain.use_case.DeleteUseCase
import com.charchil.reminderpro.domain.use_case.GetAllReminderUseCase
import com.charchil.reminderpro.domain.use_case.InsertUseCase
import com.charchil.reminderpro.domain.use_case.UpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val insertUseCase: InsertUseCase,
    private val updateUseCase: UpdateUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val getAllReminderUseCase: GetAllReminderUseCase
): ViewModel() {
    val uiState = getAllReminderUseCase.invoke().map { UiState(it) }
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, UiState())

    fun insert(reminder: Reminder) = viewModelScope.launch {
        insertUseCase.invoke(reminder)
    }

    fun update(reminder: Reminder) = viewModelScope.launch {
        updateUseCase.invoke(reminder)
    }

    fun delete(reminder: Reminder) = viewModelScope.launch {
        deleteUseCase.invoke(reminder)
    }
}
data class UiState(
    val data:List<Reminder> = emptyList()
)
