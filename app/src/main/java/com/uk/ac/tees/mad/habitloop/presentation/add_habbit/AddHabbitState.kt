package com.uk.ac.tees.mad.habitloop.presentation.add_habbit

data class AddHabbitState(
    val habitTitle: String = "",
    val description: String = "",
    val categories: List<String> = listOf("Health", "Study", "Fitness", "Creative"),
    val selectedCategory: String = "Health",
    val frequencies: List<String> = listOf("Daily", "Weekly", "Custom"),
    val selectedFrequency: String = "Daily",
    val isReminderEnabled: Boolean = false,
    val isCustomFrequencyDialogVisible: Boolean = false,
    val customFrequencyDays: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    val selectedCustomFrequencyDays: List<String> = emptyList()
)
