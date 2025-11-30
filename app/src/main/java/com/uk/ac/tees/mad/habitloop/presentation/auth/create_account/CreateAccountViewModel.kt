package com.uk.ac.tees.mad.habitloop.presentation.auth.create_account

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

class CreateAccountViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(CreateAccountState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<CreateAccountEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: CreateAccountAction) {
        when (action) {
            is CreateAccountAction.OnNameChange -> _state.update { it.copy(name = action.name) }
            is CreateAccountAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is CreateAccountAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            is CreateAccountAction.OnConfirmPasswordChange -> _state.update { it.copy(confirmPassword = action.confirmPassword) }
            CreateAccountAction.OnCreateAccountClick -> signUp()
            CreateAccountAction.OnSignInClick -> sendEvent(CreateAccountEvent.GoToLogin)
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when(val result = authRepository.signUp(
                email = state.value.email,
                password = state.value.password,
                name = state.value.name
            )) {
                is Result.Success -> sendEvent(CreateAccountEvent.Success)
                is Result.Error -> sendEvent(CreateAccountEvent.Failure(result.error))
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
