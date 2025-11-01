package com.uk.ac.tees.mad.habitloop.presentation.create_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CreateAccountViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CreateAccountState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CreateAccountState()
        )

    fun onAction(action: CreateAccountAction) {
        when (action) {
            is CreateAccountAction.OnNameChange -> _state.update { it.copy(name = action.name) }
            is CreateAccountAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is CreateAccountAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            is CreateAccountAction.OnConfirmPasswordChange -> _state.update { it.copy(confirmPassword = action.confirmPassword) }
            CreateAccountAction.OnCreateAccountClick -> { /* TODO: Handle create account logic */ }
            CreateAccountAction.OnSignInClick -> { /* TODO: Handle navigation to Sign In */ }
        }
    }
}
