package com.uk.ac.tees.mad.habitloop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: Int,
    val reminder: Long,
    val color: Int,
    val isCompleted: Boolean,
    val lastCompletedDate: Long
)
