package com.uk.ac.tees.mad.habitloop.presentation.dashboard

sealed interface DashboardAction {
    data class OnViewToggle(val isGridView: Boolean) : DashboardAction
    data class OnHabitClick(val habitId: String) : DashboardAction
}
