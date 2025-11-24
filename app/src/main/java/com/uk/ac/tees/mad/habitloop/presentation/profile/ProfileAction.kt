package com.uk.ac.tees.mad.habitloop.presentation.profile

sealed interface ProfileAction {
    object OnEditProfileClick : ProfileAction
    data class OnMotivationModeToggle(val isEnabled: Boolean) : ProfileAction
    data class OnThemeSwitchToggle(val isEnabled: Boolean) : ProfileAction
    data class OnNotificationsToggle(val isEnabled: Boolean) : ProfileAction
}
