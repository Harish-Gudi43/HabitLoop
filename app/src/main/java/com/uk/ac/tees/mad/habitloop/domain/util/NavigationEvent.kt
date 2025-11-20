package com.uk.ac.tees.mad.habitloop.domain.util

sealed interface NavigationEvent {
    data object NavigateBack : NavigationEvent
    data class NavigateToEditHabit(val habitId: String) : NavigationEvent
}
