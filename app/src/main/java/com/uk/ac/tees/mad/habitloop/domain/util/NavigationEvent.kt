package com.uk.ac.tees.mad.habitloop.domain.util

sealed interface NavigationEvent {
    data class NavigateToEditHabit(val habitId: String) : NavigationEvent
    object NavigateToLogin : NavigationEvent
    object NavigateBack : NavigationEvent
}
