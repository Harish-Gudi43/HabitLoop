package com.uk.ac.tees.mad.habitloop.presentation.dashboard

import com.uk.ac.tees.mad.habitloop.domain.models.Habit

data class DashboardState(
    val userName: String = "Alex",
    val quote: String = "The best way to predict the future is to create it.",
    val quoteAuthor: String = "Peter Drucker",
    val habits: List<Habit> = emptyList(),
    val isGridView: Boolean = false
)
