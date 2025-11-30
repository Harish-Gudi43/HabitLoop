package com.uk.ac.tees.mad.habitloop.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.util.Result
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
            LoginAction.OnForgotPasswordClick -> sendEvent(LoginEvent.GoToForgotPassword)
            LoginAction.OnCreateAccountClick -> sendEvent(LoginEvent.GoToRegister)
            LoginAction.OnUnlockWithFingerprintClick -> sendEvent(LoginEvent.ShowBiometricPrompt)
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when(val result = authRepository.signIn(state.value.email, state.value.password)) {
                is Result.Success -> sendEvent(LoginEvent.Success)
                is Result.Error -> sendEvent(LoginEvent.Failure(result.error))
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
