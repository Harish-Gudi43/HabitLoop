package com.uk.ac.tees.mad.habitloop.presentation.auth.login

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

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = LoginState()
    )

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is LoginAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            LoginAction.OnLoginClick -> login()
            LoginAction.OnCreateAccountClick -> sendEvent(LoginEvent.GoToCreateAccount)
            LoginAction.OnForgotPasswordClick -> sendEvent(LoginEvent.GoToForgotPassword)
            LoginAction.OnUnlockWithFingerprintClick -> sendEvent(LoginEvent.ShowBiometricPrompt)
        }
    }

    private fun login() {
        val email = state.value.email
        val password = state.value.password

        if (email.isEmpty() || password.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.signIn(
                email = email,
                password = password
            ).onSuccess {
                sendEvent(LoginEvent.Success)
            }.onFailure {
                sendEvent(LoginEvent.Failure)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}