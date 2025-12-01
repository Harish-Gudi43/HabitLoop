package com.uk.ac.tees.mad.habitloop.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val authRepository: AuthRepository,
    private val habitLoopRepository: HabitLoopRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(SettingState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingState()
        )

    init {
        viewModelScope.launch(ioDispatcher) {
            authRepository.getCurrentUser().collectLatest { user ->
                user?.let {
                    _state.update {
                        it.copy(
                            isBiometricSecurityOn = user.isBiometricSecurityOn,
                            isPinSecurityOn = user.isPinSecurityOn,
                            isNotificationSoundOn = user.isNotificationSoundOn,
                            isNotificationVibrationOn = user.isNotificationVibrationOn,
                            notificationFrequency = user.notificationFrequency,
                            isDailyQuoteNotificationsOn = user.isDailyQuoteNotificationsOn
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: SettingAction) {
        when (action) {
            // Security
            is SettingAction.OnBiometricSecurityToggle -> _state.update { it.copy(isBiometricSecurityOn = action.isEnabled) }
            is SettingAction.OnPinSecurityToggle -> _state.update { it.copy(isPinSecurityOn = action.isEnabled) }

            // Notifications
            is SettingAction.OnNotificationSoundToggle -> _state.update { it.copy(isNotificationSoundOn = action.isEnabled) }
            is SettingAction.OnNotificationVibrationToggle -> _state.update { it.copy(isNotificationVibrationOn = action.isEnabled) }
            is SettingAction.OnDailyQuoteNotificationsToggle -> _state.update { it.copy(isDailyQuoteNotificationsOn = action.isEnabled) }
            SettingAction.OnNotificationFrequencyClick -> { /* TODO: Show dialog to change frequency */ }

            // Data Management
            SettingAction.OnBackupDataClick -> backupData()
            SettingAction.OnRestoreDataClick -> restoreData()
            SettingAction.OnClearCacheClick -> clearCache()

            // Navigation
            SettingAction.OnBackClick -> { /* TODO: Handle navigation back */ }
        }
        updateUser()
    }

    private fun updateUser() {
        viewModelScope.launch(ioDispatcher) {
            val user = authRepository.getCurrentUser().first()
            if (user != null) {
                authRepository.updateUser(state.value.toDomain(user))
            }
        }
    }

    private fun backupData() {
        viewModelScope.launch(ioDispatcher) {
            habitLoopRepository.backupHabits()
        }
    }

    private fun restoreData() {
        viewModelScope.launch(ioDispatcher) {
            habitLoopRepository.restoreHabits()
        }
    }

    private fun clearCache() {
        viewModelScope.launch(ioDispatcher) {
            habitLoopRepository.clearHabits()
        }
    }

    private fun SettingState.toDomain(user: com.uk.ac.tees.mad.habitloop.domain.models.User) = user.copy(
        isBiometricSecurityOn = isBiometricSecurityOn,
        isPinSecurityOn = isPinSecurityOn,
        isNotificationSoundOn = isNotificationSoundOn,
        isNotificationVibrationOn = isNotificationVibrationOn,
        notificationFrequency = notificationFrequency,
        isDailyQuoteNotificationsOn = isDailyQuoteNotificationsOn
    )
}
