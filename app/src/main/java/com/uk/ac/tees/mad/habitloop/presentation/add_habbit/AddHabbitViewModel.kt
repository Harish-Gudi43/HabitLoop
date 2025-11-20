package com.uk.ac.tees.mad.habitloop.presentation.add_habbit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.data.notification.NotificationScheduler
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import com.uk.ac.tees.mad.habitloop.domain.models.Habit
import com.uk.ac.tees.mad.habitloop.domain.util.NavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class AddHabbitViewModel(
    private val repository: HabitLoopRepository,
    private val scheduler: NotificationScheduler,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddHabbitState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val habitId: String? = savedStateHandle.get<String>("id")

    init {
        if (habitId != null) {
            viewModelScope.launch {
                repository.getHabits().firstOrNull()?.find { it.id == habitId }?.let { habit ->
                    _state.update {
                        it.copy(
                            habitTitle = habit.name,
                            description = habit.description,
                            selectedCategory = habit.category,
                            selectedFrequency = habit.frequency,
                            isReminderEnabled = habit.reminder,
                            selectedCustomFrequencyDays = habit.customFrequencyDays ?: emptyList(),
                            reminderHour = habit.reminderHour,
                            reminderMinute = habit.reminderMinute
                        )
                    }
                }
            }
        }
    }

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
            is AddHabbitAction.OnTimePickerClick -> {
                _state.update { it.copy(isTimePickerVisible = true) }
            }
            is AddHabbitAction.OnTimePickerDismiss -> {
                _state.update { it.copy(isTimePickerVisible = false) }
            }
            is AddHabbitAction.OnTimeSelected -> {
                _state.update {
                    it.copy(
                        reminderHour = action.hour,
                        reminderMinute = action.minute,
                        isTimePickerVisible = false
                    )
                }
            }
        }
    }

    private fun saveHabit() {
        viewModelScope.launch {
            val habit = Habit(
                id = habitId ?: UUID.randomUUID().toString(),
                name = state.value.habitTitle,
                description = state.value.description,
                category = state.value.selectedCategory,
                frequency = state.value.selectedFrequency,
                reminder = state.value.isReminderEnabled,
                customFrequencyDays = if (state.value.selectedFrequency == "Custom") state.value.selectedCustomFrequencyDays else null,
                reminderHour = state.value.reminderHour,
                reminderMinute = state.value.reminderMinute
            )

            if (habitId == null) {
                repository.insertHabit(habit)
            } else {
                repository.updateHabit(habit)
            }

            if (habit.reminder) {
                val reminderTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, habit.reminderHour ?: 0)
                    set(Calendar.MINUTE, habit.reminderMinute ?: 0)
                    set(Calendar.SECOND, 0)
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1)
                    }
                }.timeInMillis

                scheduler.scheduleNotification(habit.name, reminderTime)
            }

            _navigationEvent.send(NavigationEvent.NavigateBack)
        }
    }
}
