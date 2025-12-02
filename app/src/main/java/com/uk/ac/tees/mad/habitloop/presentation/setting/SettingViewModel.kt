package com.uk.ac.tees.mad.habitloop.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.data.notification.NotificationScheduler
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.HabitLoopRepository
import com.uk.ac.tees.mad.habitloop.domain.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val authRepository: AuthRepository,
    private val habitLoopRepository: HabitLoopRepository,
    private val notificationScheduler: NotificationScheduler,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(SettingState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingState()
        )

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            authRepository.getCurrentUser().collectLatest { user ->
                user?.let {
                    _state.update {
                        it.copy(
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
            // Notifications
            is SettingAction.OnNotificationSoundToggle -> _state.update { it.copy(isNotificationSoundOn = action.isEnabled) }
            is SettingAction.OnNotificationVibrationToggle -> _state.update { it.copy(isNotificationVibrationOn = action.isEnabled) }
            is SettingAction.OnDailyQuoteNotificationsToggle -> {
                _state.update { it.copy(isDailyQuoteNotificationsOn = action.isEnabled) }
                if (action.isEnabled) {
                    notificationScheduler.scheduleDailyQuoteNotification()
                } else {
                    notificationScheduler.cancelDailyQuoteNotifications()
                }
            }
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
            when (habitLoopRepository.backupHabits()) {
                is Result.Success -> _toastEvent.emit("Backup successful")
                is Result.Error -> _toastEvent.emit("Backup failed")
            }
        }
    }

    private fun restoreData() {
        viewModelScope.launch(ioDispatcher) {
            when (habitLoopRepository.restoreHabits()) {
                is Result.Success -> _toastEvent.emit("Restore successful")
                is Result.Error -> _toastEvent.emit("Restore failed")
            }
        }
    }

    private fun clearCache() {
        viewModelScope.launch(ioDispatcher) {
            when (habitLoopRepository.clearHabits()) {
                is Result.Success -> _toastEvent.emit("Cache cleared")
                is Result.Error -> _toastEvent.emit("Failed to clear cache")
            }
        }
    }

    private fun SettingState.toDomain(user: com.uk.ac.tees.mad.habitloop.domain.models.User) = user.copy(
        isNotificationSoundOn = isNotificationSoundOn,
        isNotificationVibrationOn = isNotificationVibrationOn,
        notificationFrequency = notificationFrequency,
        isDailyQuoteNotificationsOn = isDailyQuoteNotificationsOn
    )
}
