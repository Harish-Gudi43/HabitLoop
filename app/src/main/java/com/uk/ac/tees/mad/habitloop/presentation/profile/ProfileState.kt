package com.uk.ac.tees.mad.habitloop.presentation.profile

data class WeeklyProgress(
    val weekLabel: String,
    val completionPercentage: Float // Value between 0.0f and 1.0f
)

data class ProfileState(
    val userName: String = "Alex Morgan",
    val profileImageRes: Int? = null, // Using Int for drawable resource, can be String for URL
    val totalHabits: Int = 25,
    val currentStreak: Int = 12,
    val longestStreak: Int = 30,
    val completionRate: Int = 85,
    val weeklyProgress: List<WeeklyProgress> = emptyList(),
    val isMotivationModeOn: Boolean = true,
    val isDarkModeOn: Boolean = true,
    val isNotificationsEnabled: Boolean = true
)
