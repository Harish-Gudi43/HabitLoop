package com.uk.ac.tees.mad.habitloop.presentation.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.SupabaseStorageRepository
import com.uk.ac.tees.mad.habitloop.domain.models.User
import com.uk.ac.tees.mad.habitloop.domain.util.NavigationEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val storageRepository: SupabaseStorageRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProfileState()
    )

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            authRepository.getCurrentUser().collectLatest { user ->
                user?.let {
                    _state.update {
                        it.copy(
                            uid = user.uid,
                            email = user.email,
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
                viewModelScope.launch(ioDispatcher) {
                    try {
                        _state.update { it.copy(isUploadingPhoto = true) }
                        val imageUrl = storageRepository.uploadProfilePicture(action.imageUri)
                        val updatedUser = state.value.toDomain().copy(profileImageUrl = imageUrl)
                        authRepository.updateUser(updatedUser)
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Failed to upload profile picture", e)
                    } finally {
                        _state.update { it.copy(isUploadingPhoto = false) }
                    }
                }
            }
            ProfileAction.OnLogoutClick -> {
                viewModelScope.launch(ioDispatcher) {
                    authRepository.logOut()
                    _navigationEvent.send(NavigationEvent.NavigateToLogin)
                }
            }
        }
    }
}

fun ProfileState.toDomain() = User(
    uid = uid,
    name = userName,
    email = email,
    profileImageUrl = profileImageUrl
)
