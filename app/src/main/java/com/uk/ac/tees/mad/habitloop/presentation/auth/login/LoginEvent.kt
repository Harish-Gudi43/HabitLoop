package com.uk.ac.tees.mad.habitloop.presentation.auth.login

import com.uk.ac.tees.mad.habitloop.domain.util.DataError

sealed interface LoginEvent {
    data object Success : LoginEvent
    data class Failure(val error: DataError) : LoginEvent
    data object GoToCreateAccount : LoginEvent
    data object GoToForgotPassword : LoginEvent
    data object ShowBiometricPrompt : LoginEvent
}
