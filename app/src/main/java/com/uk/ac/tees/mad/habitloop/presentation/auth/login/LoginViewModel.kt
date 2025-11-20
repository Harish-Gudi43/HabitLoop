package com.uk.ac.tees.mad.habitloop.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.util.HttpResult
import com.uk.ac.tees.mad.habitloop.domain.util.onFailure
import com.uk.ac.tees.mad.habitloop.domain.util.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is LoginAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            LoginAction.OnLoginClick -> login()
            LoginAction.OnCreateAccountClick -> sendEvent(LoginEvent.GoToCreateAccount)
            LoginAction.OnForgotPasswordClick -> sendEvent(LoginEvent.GoToForgotPassword)
            LoginAction.OnUnlockWithFingerprintClick -> {
                // TODO: Handle fingerprint unlock
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.signIn(
                email = state.value.email,
                password = state.value.password
            ).onSuccess {
                sendEvent(LoginEvent.Success)
            }.onFailure {
                sendEvent(LoginEvent.Failure(it))
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
