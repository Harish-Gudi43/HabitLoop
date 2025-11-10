package com.uk.ac.tees.mad.habitloop.presentation.auth.create_account

sealed class CreateAccountEvent {
    data object Success: CreateAccountEvent()
    data object Failure: CreateAccountEvent()
    data object GoToLogin: CreateAccountEvent()
}