package com.uk.ac.tees.mad.habitloop.presentation.dashboard

import com.uk.ac.tees.mad.habitloop.domain.models.Habit

data class DashboardState(
    val userName: String = "Alex",
    val quote: String = "",
    val quoteAuthor: String = "",
    val habits: List<Habit> = emptyList(),
    val isGridView: Boolean = false,
    val isRefreshing: Boolean = false
)
