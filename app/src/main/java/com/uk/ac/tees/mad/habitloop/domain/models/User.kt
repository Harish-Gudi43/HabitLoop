package com.uk.ac.tees.mad.habitloop.domain.models

import com.uk.ac.tees.mad.habitloop.data.local.UserEntity
import kotlinx.serialization.Serializable

// Domain model
data class User(
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

// DTO for Firestore
@Serializable
data class UserDto(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val motivationMode: Boolean = true,
    val isBiometricSecurityOn: Boolean = false,
    val isPinSecurityOn: Boolean = true,
    val isNotificationSoundOn: Boolean = true,
    val isNotificationVibrationOn: Boolean = true,
    val notificationFrequency: String = "Daily",
    val isDailyQuoteNotificationsOn: Boolean = true
) {
    fun toDomain() = User(
        uid,
        name,
        email,
        profileImageUrl,
        motivationMode,
        isBiometricSecurityOn,
        isPinSecurityOn,
        isNotificationSoundOn,
        isNotificationVibrationOn,
        notificationFrequency,
        isDailyQuoteNotificationsOn
    )
}

// Mappers
fun User.toEntity() = UserEntity(
    uid,
    name,
    email,
    profileImageUrl,
    motivationMode,
    isBiometricSecurityOn,
    isPinSecurityOn,
    isNotificationSoundOn,
    isNotificationVibrationOn,
    notificationFrequency,
    isDailyQuoteNotificationsOn
)

fun UserEntity.toDomain() = User(
    uid,
    name,
    email,
    profileImageUrl,
    motivationMode,
    isBiometricSecurityOn,
    isPinSecurityOn,
    isNotificationSoundOn,
    isNotificationVibrationOn,
    notificationFrequency,
    isDailyQuoteNotificationsOn
)
