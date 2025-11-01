package com.uk.ac.tees.mad.habitloop.presentation.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ForgotViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ForgotState())
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
            initialValue = ForgotState()
        )

    fun onAction(action: ForgotAction) {
        when (action) {
            is ForgotAction.OnEmailChange -> {
                _state.update { it.copy(email = action.email) }
            }
            ForgotAction.OnSubmitClick -> {
                // TODO: Handle password reset submission logic
            }
            ForgotAction.OnBackToLoginClick -> {
                // TODO: Handle navigation back to login
            }
            ForgotAction.OnBackArrowClick -> {
                // TODO: Handle navigation for back arrow
            }
        }
    }
}
