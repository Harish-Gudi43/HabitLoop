package com.uk.ac.tees.mad.habitloop.presentation.profile

import android.net.Uri

sealed interface ProfileAction {
    object OnEditProfileClick : ProfileAction
    data class OnMotivationModeToggle(val isEnabled: Boolean) : ProfileAction
    data class OnThemeSwitchToggle(val isEnabled: Boolean) : ProfileAction
    data class OnNotificationsToggle(val isEnabled: Boolean) : ProfileAction
    object OnLogoutClick : ProfileAction
    data class OnProfileImageChange(val imageUri: Uri) : ProfileAction
}
