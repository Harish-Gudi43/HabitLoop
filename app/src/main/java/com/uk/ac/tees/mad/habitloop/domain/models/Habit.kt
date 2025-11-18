package com.uk.ac.tees.mad.habitloop.domain.models

import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val category: String,
    val frequency: String,
    val reminder: Boolean,
    val customFrequencyDays: List<String>? = null,
    val isCompleted: Boolean = false,
    val streak: Int = 0,
    val nextOccurrence: String = "",
    val lastCompletedDate: Long = 0L // To track streaks
)
