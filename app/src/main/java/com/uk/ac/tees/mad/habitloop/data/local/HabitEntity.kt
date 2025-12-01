package com.uk.ac.tees.mad.habitloop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val frequency: String,
    val reminder: Boolean,
    val customFrequencyDays: List<String>? = null,
    val isCompleted: Boolean = false,
    val streak: Int = 0,
    val nextOccurrence: String = "",
    val lastCompletedDate: Long = 0L,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null
)
