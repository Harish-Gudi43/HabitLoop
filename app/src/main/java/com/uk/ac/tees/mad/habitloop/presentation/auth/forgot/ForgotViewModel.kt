package com.uk.ac.tees.mad.habitloop.presentation.auth.forgot

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

class ForgotViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(ForgotState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<ForgotEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ForgotAction) {
        when (action) {
            is ForgotAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            ForgotAction.OnSubmitClick -> forgotPassword()
            ForgotAction.OnBackToLoginClick -> sendEvent(ForgotEvent.BackToLogin)
        }
    }

    private fun forgotPassword() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when(val result = authRepository.forgotPassword(state.value.email)) {
                is Result.Success -> sendEvent(ForgotEvent.Success)
                is Result.Error -> sendEvent(ForgotEvent.Failure(result.error))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendEvent(event: ForgotEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}
