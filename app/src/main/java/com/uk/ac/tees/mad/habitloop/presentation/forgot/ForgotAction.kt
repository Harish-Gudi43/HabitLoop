package com.uk.ac.tees.mad.habitloop.presentation.forgot

sealed interface ForgotAction {
    data class OnEmailChange(val email: String) : ForgotAction
    object OnSubmitClick : ForgotAction
    object OnBackToLoginClick : ForgotAction
    object OnBackArrowClick : ForgotAction
}
