package com.uk.ac.tees.mad.habitloop.presentation.dashboard

data class Habit(
    val id: String,
    val name: String,
    val isCompleted: Boolean,
    val streak: Int,
    val nextOccurrence: String
)

data class DashboardState(
    val userName: String = "Alex",
    val quote: String = "The best way to predict the future is to create it.",
    val quoteAuthor: String = "Peter Drucker",
    val habits: List<Habit> = emptyList(),
    val isGridView: Boolean = false
)
