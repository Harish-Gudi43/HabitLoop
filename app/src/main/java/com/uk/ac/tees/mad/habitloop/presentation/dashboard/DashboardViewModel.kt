package com.uk.ac.tees.mad.habitloop.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import com.uk.ac.tees.mad.habitloop.domain.QuoteRepository
import com.uk.ac.tees.mad.habitloop.domain.models.Habit
import com.uk.ac.tees.mad.habitloop.domain.util.NavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(
    private val habitRepository: HabitLoopRepository,
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        refreshData()
        viewModelScope.launch {
            habitRepository.getHabits().collectLatest { habits ->
                val today = Calendar.getInstance()
                val updatedHabits = habits.map { habit ->
                    val lastCompleted = Calendar.getInstance().apply { timeInMillis = habit.lastCompletedDate }
                    habit.copy(isCompleted = isSameDay(today, lastCompleted))
                }
                _state.update { it.copy(habits = updatedHabits) }
            }
        }

        viewModelScope.launch {
            quoteRepository.getQuote().collectLatest { quote ->
                quote?.let {
                    _state.update { it.copy(quote = quote.text, quoteAuthor = quote.author) }
                }
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

                    val today = Calendar.getInstance()
                    val lastCompletedCal = Calendar.getInstance().apply { timeInMillis = habit.lastCompletedDate }
                    val isCompletedToday = isSameDay(today, lastCompletedCal)

                    val updatedHabit = if (!isCompletedToday) {
                        // Habit is not completed today, so complete it
                        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                        val newStreak = if (isSameDay(lastCompletedCal, yesterday)) habit.streak + 1 else 1
                        habit.copy(
                            lastCompletedDate = today.timeInMillis,
                            streak = newStreak
                        )
                    } else {
                        // Habit is already completed today, so un-complete it
                        habit.copy(
                            lastCompletedDate = 0L, // Reset to re-allow completion today
                            streak = (habit.streak - 1).coerceAtLeast(0)
                        )
                    }
                    habitRepository.updateHabit(updatedHabit)
                }
            }
            is DashboardAction.OnEditClick -> {
                viewModelScope.launch {
                    _navigationEvent.send(NavigationEvent.NavigateToEditHabit(action.habitId))
                }
            }
            is DashboardAction.OnRefresh -> {
                refreshData()
            }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            try {
                habitRepository.syncWithFirebase()
            } catch (e: Exception) {
                // In a real app, you would log this error
            }
            try {
                quoteRepository.refreshQuote()
            } catch (e: Exception) {
                // In a real app, you would log this error
            }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
