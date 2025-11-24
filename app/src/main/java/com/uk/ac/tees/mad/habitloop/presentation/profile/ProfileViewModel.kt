package com.uk.ac.tees.mad.habitloop.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnMotivationModeToggle -> {
                _state.update { it.copy(isMotivationModeOn = action.isEnabled) }
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
        }
    }

    private fun loadInitialData() {
        _state.update {
            it.copy(
                profileImageRes = R.drawable.ic_profile_placeholder,
                weeklyProgress = listOf(
                    WeeklyProgress("Week 1", 0.70f),
                    WeeklyProgress("Week 2", 0.85f),
                    WeeklyProgress("Week 3", 0.75f),
                    WeeklyProgress("Week 4", 0.90f)
                )
            )
        }
    }
}
