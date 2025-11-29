package com.uk.ac.tees.mad.habitloop.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.R
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.SupabaseStorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val storageRepository: SupabaseStorageRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { user ->
                user?.let {
                    _state.update {
                        it.copy(
                            userName = user.name,
                            profileImageUrl = user.profileImageUrl
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnMotivationModeToggle -> {
                _state.update { it.copy(isMotivationModeOn = action.isEnabled) }
            }
            is ProfileAction.OnThemeSwitchToggle -> {
                _state.update { it.copy(isDarkModeOn = action.isEnabled) }
                // TODO: Add logic here to change the actual app theme
            }
            is ProfileAction.OnNotificationsToggle -> {
                _state.update { it.copy(isNotificationsEnabled = action.isEnabled) }
            }
            ProfileAction.OnEditProfileClick -> {
                // TODO: Handle navigation to an edit profile screen
            }
            is ProfileAction.OnProfileImageChange -> {
                viewModelScope.launch {
                    val imageUrl = storageRepository.uploadProfilePicture(action.imageUri)
                    val user = authRepository.getCurrentUser().value
                    user?.let {
                        val updatedUser = it.copy(profileImageUrl = imageUrl)
                        authRepository.updateUser(updatedUser)
                    }
                }
            }
            ProfileAction.OnLogoutClick -> {
                viewModelScope.launch {
                    authRepository.logOut()
                }
            }
        }
    }

    private fun loadInitialData() {
        _state.update {
            it.copy(
                profileImageRes = R.drawable.ic_profile_placeholder,
                weeklyProgress = listOf(
                    WeeklyProgress("Week 1", 0.70f),
                    WeeklyProgress("Week 2", 0.85f),
                    WeeklyProgress("Week 3", 0.75f),
                    WeeklyProgress("Week 4", 0.90f)
                )
            )
        }
    }
}
