package com.uk.ac.tees.mad.habitloop.domain.models

import com.google.firebase.firestore.PropertyName
import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val frequency: String = "",
    val reminder: Boolean = false,
    val customFrequencyDays: List<String>? = null,
    @get:PropertyName("completed")
    val isCompleted: Boolean = false,
    val streak: Int = 0,
    val nextOccurrence: String = "",
    val lastCompletedDate: Long = 0L, // To track streaks
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null
)
