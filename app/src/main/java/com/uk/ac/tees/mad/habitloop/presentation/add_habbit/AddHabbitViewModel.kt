package com.uk.ac.tees.mad.habitloop.presentation.add_habbit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import com.uk.ac.tees.mad.habitloop.domain.models.Habit
import com.uk.ac.tees.mad.habitloop.domain.util.NavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddHabbitViewModel(
    private val repository: HabitLoopRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddHabbitState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

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
                saveHabit()
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

    private fun saveHabit() {
        viewModelScope.launch {
            val habit = Habit(
                name = state.value.habitTitle,
                description = state.value.description,
                category = state.value.selectedCategory,
                frequency = state.value.selectedFrequency,
                reminder = state.value.isReminderEnabled,
                customFrequencyDays = if (state.value.selectedFrequency == "Custom") state.value.selectedCustomFrequencyDays else null
            )
            repository.insertHabit(habit)
            _navigationEvent.send(NavigationEvent.NavigateBack)
        }
    }
}
