package com.uk.ac.tees.mad.habitloop.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DashboardState()
        )

    init {
        loadInitialData()
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.OnViewToggle -> {
                _state.update { it.copy(isGridView = action.isGridView) }
            }
            is DashboardAction.OnHabitClick -> {
                _state.update { currentState ->
                    val updatedHabits = currentState.habits.map { habit ->
                        if (habit.id == action.habitId) {
                            habit.copy(isCompleted = !habit.isCompleted)
                        } else {
                            habit
                        }
                    }
                    currentState.copy(habits = updatedHabits)
                }
            }
        }
    }

    private fun loadInitialData() {
        _state.update {
            it.copy(
                habits = listOf(
                    Habit("1", "Morning Run", true, 15, "6:00 AM"),
                    Habit("2", "Read Book", false, 7, "9:00 PM"),
                    Habit("3", "Drink Water", true, 30, "2:00 PM"),
                    Habit("4", "Meditate", false, 3, "7:00 AM"),
                    Habit("5", "Learn React", true, 5, "5:00 PM"),
                    Habit("6", "Call Mom", false, 1, "8:00 PM"),
                )
            )
        }
    }
}
