package com.uk.ac.tees.mad.habitloop.domain.util

sealed interface NavigationEvent {
    data object NavigateBack : NavigationEvent
}
