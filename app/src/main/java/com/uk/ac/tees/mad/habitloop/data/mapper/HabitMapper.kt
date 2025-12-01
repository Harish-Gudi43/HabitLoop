package com.uk.ac.tees.mad.habitloop.data.mapper

import com.uk.ac.tees.mad.habitloop.data.local.HabitEntity
import com.uk.ac.tees.mad.habitloop.domain.models.Habit

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        description = description,
        category = category,
        frequency = frequency,
        reminder = reminder,
        customFrequencyDays = customFrequencyDays,
        isCompleted = completed,
        streak = streak,
        nextOccurrence = nextOccurrence,
        lastCompletedDate = lastCompletedDate,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )
}

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        description = description,
        category = category,
        frequency = frequency,
        reminder = reminder,
        customFrequencyDays = customFrequencyDays,
        completed = isCompleted,
        streak = streak,
        nextOccurrence = nextOccurrence,
        lastCompletedDate = lastCompletedDate,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )
}
