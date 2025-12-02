package com.uk.ac.tees.mad.habitloop.domain.models

import com.google.firebase.firestore.PropertyName
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
    @get:PropertyName("motivation_mode")
    @set:PropertyName("motivation_mode")
    var motivationMode: Boolean = true,
    @get:PropertyName("is_biometric_security_on")
    @set:PropertyName("is_biometric_security_on")
    var isBiometricSecurityOn: Boolean = false,
    @get:PropertyName("is_pin_security_on")
    @set:PropertyName("is_pin_security_on")
    var isPinSecurityOn: Boolean = true,
    @get:PropertyName("is_notification_sound_on")
    @set:PropertyName("is_notification_sound_on")
    var isNotificationSoundOn: Boolean = true,
    @get:PropertyName("is_notification_vibration_on")
    @set:PropertyName("is_notification_vibration_on")
    var isNotificationVibrationOn: Boolean = true,
    @get:PropertyName("notification_frequency")
    @set:PropertyName("notification_frequency")
    var notificationFrequency: String = "Daily",
    @get:PropertyName("is_daily_quote_notifications_on")
    @set:PropertyName("is_daily_quote_notifications_on")
    var isDailyQuoteNotificationsOn: Boolean = true
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
