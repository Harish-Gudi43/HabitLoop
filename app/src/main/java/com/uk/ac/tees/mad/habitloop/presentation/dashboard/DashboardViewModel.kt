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
import kotlinx.coroutines.flow.firstOrNull
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
        // 1. Load habits once into local UI state
        viewModelScope.launch {
            loadHabitsSnapshot()
        }

        // 2. Observe quote continuously (safe, doesn't stomp habits UI)
        viewModelScope.launch {
            quoteRepository.getQuote().collectLatest { quote ->
                quote?.let {
                    _state.update { st ->
                        st.copy(
                            quote = quote.text,
                            quoteAuthor = quote.author
                        )
                    }
                }
            }
        }

        // 3. Initial sync / refresh
        refreshData()
    }

    fun onAction(action: DashboardAction) {
        when (action) {

            is DashboardAction.OnViewToggle -> {
                _state.update { it.copy(isGridView = action.isGridView) }
            }

            is DashboardAction.OnHabitClick -> {
                val currentHabits = state.value.habits
                val habitToUpdate = currentHabits.find { it.id == action.habitId } ?: return

                // -- figure out if it's completed today --
                val completedToday = isTimestampToday(habitToUpdate.lastCompletedDate)

                val updatedHabit: Habit = if (!completedToday) {
                    // Mark as done for today
                    val nowMillis = System.currentTimeMillis()

                    // did we also complete it yesterday? (for streak carry forward)
                    val wasYesterday = wasTimestampYesterday(habitToUpdate.lastCompletedDate)

                    val newStreak = if (wasYesterday) {
                        habitToUpdate.streak + 1
                    } else {
                        1
                    }

                    habitToUpdate.copy(
                        lastCompletedDate = nowMillis, // ALWAYS millis
                        streak = newStreak
                    )
                } else {
                    // Undo today's completion
                    habitToUpdate.copy(
                        lastCompletedDate = 0L,
                        streak = (habitToUpdate.streak - 1).coerceAtLeast(0)
                    )
                }

                // Optimistically update local state and KEEP IT
                val newList = currentHabits.map { old ->
                    if (old.id == updatedHabit.id) updatedHabit else old
                }

                _state.update { st ->
                    st.copy(habits = newList)
                }

                // Persist in background
                viewModelScope.launch {
                    try {
                        habitRepository.updateHabit(updatedHabit)
                    } catch (_: Exception) {
                        // optional: rollback if you want
                    }
                }
            }

            is DashboardAction.OnEditClick -> {
                viewModelScope.launch {
                    _navigationEvent.send(
                        NavigationEvent.NavigateToEditHabit(action.habitId)
                    )
                }
            }

            DashboardAction.OnRefresh -> {
                refreshData()
            }
        }
    }

    /**
     * Pull-to-refresh / first load sequence:
     * 1. show spinner
     * 2. sync with backend
     * 3. reload habits snapshot into state
     * 4. refresh quote (best effort)
     */
    private fun refreshData() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            try {
                habitRepository.syncWithFirebase()
            } catch (_: Exception) {
                // ignore
            }

            // after syncing, grab a fresh one-time snapshot (not continuous collect)
            loadHabitsSnapshot()

            try {
                quoteRepository.refreshQuote()
            } catch (_: Exception) {
                // ignore
            }

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    /**
     * Loads the current list of habits ONCE from the repository,
     * normalizes timestamps to millis (in case DB stored seconds),
     * and stores them into state.
     *
     * We DO NOT keep collecting forever. That would stomp optimistic UI.
     */
    private suspend fun loadHabitsSnapshot() {
        val snapshot = habitRepository.getHabits().firstOrNull().orEmpty()

        val normalized = snapshot.map { h ->
            h.copy(
                lastCompletedDate = normalizeToMillis(h.lastCompletedDate)
            )
        }

        _state.update { it.copy(habits = normalized) }
    }

    // -----------------------------------------------------------------
    // Time helpers (Calendar-based, so no java.time / no API 26 needed)
    // -----------------------------------------------------------------

    /**
     * Convert seconds → millis if needed.
     * Seconds ~ 1_700_000_000 (10 digits)
     * Millis ~ 1_700_000_000_000 (13 digits)
     */
    private fun normalizeToMillis(ts: Long): Long {
        return if (ts in 1..10_000_000_000L) ts * 1000 else ts
    }

    /**
     * True if lastCompletedTs (seconds OR millis) is "today" for the current device tz.
     */
    private fun isTimestampToday(lastCompletedTs: Long): Boolean {
        val millis = normalizeToMillis(lastCompletedTs)
        if (millis <= 0L) return false

        val now = Calendar.getInstance()
        val last = Calendar.getInstance().apply { timeInMillis = millis }

        return isSameDay(now, last)
    }

    /**
     * True if lastCompletedTs happened "yesterday" (1 day before today)
     * in the device tz. Used for streak calculation.
     */
    private fun wasTimestampYesterday(lastCompletedTs: Long): Boolean {
        val millis = normalizeToMillis(lastCompletedTs)
        if (millis <= 0L) return false

        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val last = Calendar.getInstance().apply { timeInMillis = millis }

        return isSameDay(yesterday, last)
    }

    /**
     * Calendar day compare (year + dayOfYear).
     */
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
