package com.uk.ac.tees.mad.habitloop.presentation.auth.login

import com.uk.ac.tees.mad.habitloop.domain.util.DataError

sealed interface LoginEvent {
    object Success : LoginEvent
    data class Failure(val error: DataError.Firebase) : LoginEvent
    object GoToForgotPassword : LoginEvent
    object GoToRegister : LoginEvent
    object ShowBiometricPrompt : LoginEvent
}
