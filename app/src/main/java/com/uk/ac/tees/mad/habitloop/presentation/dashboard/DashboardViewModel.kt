package com.uk.ac.tees.mad.habitloop.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(
    private val repository: HabitLoopRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getHabits().collectLatest { habits ->
                _state.update { it.copy(habits = habits) }
            }
        }
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.OnViewToggle -> {
                _state.update { it.copy(isGridView = action.isGridView) }
            }
            is DashboardAction.OnHabitClick -> {
                viewModelScope.launch {
                    val habit = state.value.habits.find { it.id == action.habitId } ?: return@launch
                    val isCompleted = !habit.isCompleted
                    val today = Calendar.getInstance()
                    val lastCompleted = Calendar.getInstance().apply { timeInMillis = habit.lastCompletedDate }

                    val newStreak = if (isCompleted) {
                        if (!isSameDay(today, lastCompleted)) {
                            habit.streak + 1
                        } else {
                            habit.streak // Already completed today
                        }
                    } else {
                        if (isSameDay(today, lastCompleted)) {
                            habit.streak - 1
                        } else {
                            habit.streak // Not completed today, so streak doesn't change
                        }
                    }

                    val updatedHabit = habit.copy(
                        isCompleted = isCompleted,
                        streak = newStreak,
                        lastCompletedDate = if (isCompleted) System.currentTimeMillis() else habit.lastCompletedDate
                    )
                    repository.updateHabit(updatedHabit)
                }
            }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
