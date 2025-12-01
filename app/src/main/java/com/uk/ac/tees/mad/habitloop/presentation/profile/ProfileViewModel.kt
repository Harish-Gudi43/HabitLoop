package com.uk.ac.tees.mad.habitloop.presentation.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import com.uk.ac.tees.mad.habitloop.domain.QuoteRepository
import com.uk.ac.tees.mad.habitloop.domain.SupabaseStorageRepository
import com.uk.ac.tees.mad.habitloop.domain.models.Habit
import com.uk.ac.tees.mad.habitloop.domain.models.Quote
import com.uk.ac.tees.mad.habitloop.domain.models.User
import com.uk.ac.tees.mad.habitloop.domain.util.NavigationEvent
import com.uk.ac.tees.mad.habitloop.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.WeekFields
import java.util.Locale

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val storageRepository: SupabaseStorageRepository,
    private val habitLoopRepository: HabitLoopRepository,
    private val quoteRepository: QuoteRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProfileState()
    )

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            authRepository.getCurrentUser().collectLatest { user ->
                user?.let {
                    _state.update {
                        it.copy(
                            uid = user.uid,
                            email = user.email,
                            userName = user.name,
                            profileImageUrl = user.profileImageUrl,
                            isMotivationModeOn = user.motivationMode
                        )
                    }
                }
            }
        }

        viewModelScope.launch(ioDispatcher) {
            habitLoopRepository.getHabits().collectLatest { habits ->
                val totalHabits = habits.size
                val completedHabits = habits.count { it.completed }
                val completionRate = if (totalHabits > 0) (completedHabits * 100 / totalHabits) else 0
                val currentStreak = habits.maxOfOrNull { it.streak } ?: 0
                val longestStreak = habits.maxOfOrNull { it.streak } ?: 0 // Assuming longest streak is the max streak for now

                _state.update {
                    it.copy(
                        totalHabits = totalHabits,
                        completionRate = completionRate,
                        currentStreak = currentStreak,
                        longestStreak = longestStreak,
                        weeklyProgress = calculateWeeklyProgress(habits)
                    )
                }
            }
        }

        viewModelScope.launch(ioDispatcher) {
            quoteRepository.getQuote().collectLatest { result ->
                if (result is Result.Success<*>) {
                    val quoteText = (result.data as? Quote)?.text
                    if (quoteText != null) {
                        _state.update { it.copy(motivationalQuote = quoteText) }
                    }
                }
            }
        }
    }

    private fun calculateWeeklyProgress(habits: List<Habit>): List<WeeklyProgress> {
        val weekFields = WeekFields.of(Locale.getDefault())
        val habitsByWeek = habits.groupBy {
            try {
                if (it.nextOccurrence.isNotBlank()) {
                    val date = LocalDate.parse(it.nextOccurrence)
                    date.get(weekFields.weekOfWeekBasedYear())
                } else {
                    -1
                }
            } catch (e: DateTimeParseException) {
                -1 // or some other default value for invalid date format
            }
        }

        return habitsByWeek.filterKeys { it != -1 }.map {
            val weekNumber = it.key
            val habitsInWeek = it.value
            val completedInWeek = habitsInWeek.count { habit -> habit.completed }
            val completionPercentage = if (habitsInWeek.isNotEmpty()) {
                (completedInWeek.toFloat() / habitsInWeek.size)
            } else {
                0f
            }
            WeeklyProgress("Week $weekNumber", completionPercentage)
        }.sortedBy { it.weekLabel }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnMotivationModeToggle -> {
                viewModelScope.launch(ioDispatcher) {
                    _state.update { it.copy(isMotivationModeOn = action.isEnabled) }
                    val updatedUser = state.value.toDomain().copy(motivationMode = action.isEnabled)
                    authRepository.updateUser(updatedUser)
                }
            }
            is ProfileAction.OnThemeSwitchToggle -> {
                _state.update { it.copy(isDarkModeOn = action.isEnabled) }
                // TODO: Add logic here to change the actual app theme
            }
            is ProfileAction.OnNotificationsToggle -> {
                _state.update { it.copy(isNotificationsEnabled = action.isEnabled) }
            }
            ProfileAction.OnEditProfileClick -> {
                // TODO: Handle navigation to an edit profile screen
            }
            is ProfileAction.OnProfileImageChange -> {
                viewModelScope.launch(ioDispatcher) {
                    try {
                        _state.update { it.copy(isUploadingPhoto = true) }
                        val imageUrl = storageRepository.uploadProfilePicture(action.imageUri)
                        val updatedUser = state.value.toDomain().copy(profileImageUrl = imageUrl)
                        authRepository.updateUser(updatedUser)
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Failed to upload profile picture", e)
                    } finally {
                        _state.update { it.copy(isUploadingPhoto = false) }
                    }
                }
            }
            ProfileAction.OnLogoutClick -> {
                viewModelScope.launch(ioDispatcher) {
                    authRepository.logOut()
                    _navigationEvent.send(NavigationEvent.NavigateToLogin)
                }
            }
        }
    }
}

fun ProfileState.toDomain() = User(
    uid = uid,
    name = userName,
    email = email,
    profileImageUrl = profileImageUrl,
    motivationMode = isMotivationModeOn
)
