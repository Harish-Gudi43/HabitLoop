package com.uk.ac.tees.mad.habitloop.presentation.auth.create_account

sealed interface CreateAccountAction {
    data class OnNameChange(val name: String) : CreateAccountAction
    data class OnEmailChange(val email: String) : CreateAccountAction
    data class OnPasswordChange(val password: String) : CreateAccountAction
    data class OnConfirmPasswordChange(val confirmPassword: String) : CreateAccountAction
    object OnCreateAccountClick : CreateAccountAction
    object OnSignInClick : CreateAccountAction
}
