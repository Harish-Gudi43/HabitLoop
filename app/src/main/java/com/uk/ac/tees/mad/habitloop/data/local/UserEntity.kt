package com.uk.ac.tees.mad.habitloop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null,
    val motivationMode: Boolean = true,
    val isBiometricSecurityOn: Boolean = false,
    val isPinSecurityOn: Boolean = true,
    val isNotificationSoundOn: Boolean = true,
    val isNotificationVibrationOn: Boolean = true,
    val notificationFrequency: String = "Daily",
    val isDailyQuoteNotificationsOn: Boolean = true
)
