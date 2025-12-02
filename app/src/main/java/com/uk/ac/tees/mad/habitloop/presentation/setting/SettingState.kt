package com.uk.ac.tees.mad.habitloop.presentation.setting

data class SettingState(
    // Notification Preferences
    val isNotificationSoundOn: Boolean = true,
    val isNotificationVibrationOn: Boolean = true,
    val notificationFrequency: String = "Daily",
    val isDailyQuoteNotificationsOn: Boolean = true
)
