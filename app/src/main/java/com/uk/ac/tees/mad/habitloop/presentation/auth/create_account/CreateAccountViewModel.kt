package com.uk.ac.tees.mad.habitloop.presentation.auth.create_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.bookly.domain.util.onFailure
import uk.ac.tees.mad.bookly.domain.util.onSuccess

class CreateAccountViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(CreateAccountState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CreateAccountState()
    )

    private val eventChannel = Channel<CreateAccountEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: CreateAccountAction) {
        when (action) {
            is CreateAccountAction.OnNameChange -> _state.update { it.copy(name = action.name) }
            is CreateAccountAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is CreateAccountAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            is CreateAccountAction.OnConfirmPasswordChange -> _state.update { it.copy(confirmPassword = action.confirmPassword) }
            CreateAccountAction.OnCreateAccountClick -> createAccount()
            CreateAccountAction.OnSignInClick -> sendEvent(CreateAccountEvent.GoToLogin)
        }
    }

    private fun createAccount() {
        val currentState = state.value
        if (currentState.password != currentState.confirmPassword) {
            sendEvent(CreateAccountEvent.Failure) // Or a more specific event
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.signUp(
                name = currentState.name,
                email = currentState.email,
                password = currentState.password
            ).onSuccess {
                sendEvent(CreateAccountEvent.Success)
            }.onFailure {
                sendEvent(CreateAccountEvent.Failure)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendEvent(event: CreateAccountEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}