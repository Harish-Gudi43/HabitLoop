package com.uk.ac.tees.mad.habitloop.domain.models

import com.uk.ac.tees.mad.habitloop.R
import com.uk.ac.tees.mad.habitloop.data.local.HabitEntity
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: Int,
    val reminder: Long,
    val color: Int,
    val isCompleted: Boolean = false, // Added for stats
    val lastCompletedDate: Long = 0L   // Added for stats
)
