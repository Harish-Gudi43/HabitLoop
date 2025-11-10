package com.uk.ac.tees.mad.habitloop.presentation.auth.login

sealed interface LoginAction {
    data class OnEmailChange(val email: String) : LoginAction
    data class OnPasswordChange(val password: String) : LoginAction
    object OnLoginClick : LoginAction
    object OnCreateAccountClick : LoginAction
    object OnForgotPasswordClick : LoginAction
    object OnUnlockWithFingerprintClick : LoginAction
}