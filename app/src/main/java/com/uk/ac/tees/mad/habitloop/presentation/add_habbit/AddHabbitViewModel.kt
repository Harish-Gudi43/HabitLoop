package com.uk.ac.tees.mad.habitloop.presentation.add_habbit

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddHabbitViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddHabbitState())
    val state = _state.asStateFlow()

    fun onAction(action: AddHabbitAction) {
        when (action) {
            is AddHabbitAction.OnTitleChange -> {
                _state.update { it.copy(habitTitle = action.title) }
            }
            is AddHabbitAction.OnCategoryChange -> {
                _state.update { it.copy(selectedCategory = action.category) }
            }
            is AddHabbitAction.OnFrequencyChange -> {
                _state.update { it.copy(selectedFrequency = action.frequency) }
            }
            is AddHabbitAction.OnDescriptionChange -> {
                _state.update { it.copy(description = action.description) }
            }
            is AddHabbitAction.OnReminderToggle -> {
                _state.update { it.copy(isReminderEnabled = action.isEnabled) }
            }
            is AddHabbitAction.OnSaveClick -> {
                // TODO: Handle save
            }
            is AddHabbitAction.OnCustomFrequencyClick -> {
                _state.update { it.copy(isCustomFrequencyDialogVisible = true) }
            }
            is AddHabbitAction.OnCustomFrequencyDialogDismiss -> {
                _state.update { it.copy(isCustomFrequencyDialogVisible = false) }
            }
            is AddHabbitAction.OnCustomFrequencyDaySelected -> {
                _state.update { currentState ->
                    val selectedDays = currentState.selectedCustomFrequencyDays.toMutableList()
                    if (selectedDays.contains(action.day)) {
                        selectedDays.remove(action.day)
                    } else {
                        selectedDays.add(action.day)
                    }
                    currentState.copy(selectedCustomFrequencyDays = selectedDays)
                }
            }
        }
    }
}
