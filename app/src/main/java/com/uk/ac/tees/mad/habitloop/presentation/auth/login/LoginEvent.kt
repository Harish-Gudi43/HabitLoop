package com.uk.ac.tees.mad.habitloop.presentation.auth.login

sealed class LoginEvent {
    data object Success: LoginEvent()
    data object Failure: LoginEvent()
    data object GoToCreateAccount: LoginEvent()
    data object GoToForgotPassword: LoginEvent()
    data object ShowBiometricPrompt: LoginEvent()
    data object BiometricAuthNotAvailable: LoginEvent()
}
