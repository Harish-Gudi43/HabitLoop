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
        customFrequencyDays = customFrequencyDays
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
        customFrequencyDays = customFrequencyDays
    )
}
