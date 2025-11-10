package com.uk.ac.tees.mad.habitloop.presentation.auth.create_account

data class CreateAccountState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false
)