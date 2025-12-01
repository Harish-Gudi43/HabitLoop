package com.uk.ac.tees.mad.habitloop.presentation.profile

data class WeeklyProgress(
    val weekLabel: String,
    val completionPercentage: Float // Value between 0.0f and 1.0f
)

data class ProfileState(
    val uid: String = "",
    val email: String = "",
    val userName: String = "",
    val profileImageUrl: String? = null,
    val totalHabits: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completionRate: Int = 0,
    val weeklyProgress: List<WeeklyProgress> = emptyList(),
    val isMotivationModeOn: Boolean = true,
    val isDarkModeOn: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    val isUploadingPhoto: Boolean = false,
    val motivationalQuote: String = ""
)
