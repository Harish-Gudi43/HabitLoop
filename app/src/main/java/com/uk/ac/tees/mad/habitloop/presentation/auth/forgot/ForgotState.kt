package com.uk.ac.tees.mad.habitloop.presentation.auth.forgot

data class ForgotState(
    val email: String = "",
    val isLoading: Boolean = false
)