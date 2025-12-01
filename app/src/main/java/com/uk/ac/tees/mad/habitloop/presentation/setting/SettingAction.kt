package com.uk.ac.tees.mad.habitloop.presentation.setting

sealed interface SettingAction {
    // Security
    data class OnBiometricSecurityToggle(val isEnabled: Boolean) : SettingAction
    data class OnPinSecurityToggle(val isEnabled: Boolean) : SettingAction

    // Notifications
    data class OnNotificationSoundToggle(val isEnabled: Boolean) : SettingAction
    data class OnNotificationVibrationToggle(val isEnabled: Boolean) : SettingAction
    object OnNotificationFrequencyClick : SettingAction
    data class OnDailyQuoteNotificationsToggle(val isEnabled: Boolean) : SettingAction

    // Data Management
    object OnBackupDataClick : SettingAction
    object OnRestoreDataClick : SettingAction
    object OnClearCacheClick : SettingAction

    // Navigation
    object OnBackClick : SettingAction
}
