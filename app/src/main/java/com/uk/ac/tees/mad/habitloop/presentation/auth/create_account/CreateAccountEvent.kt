package com.uk.ac.tees.mad.habitloop.presentation.auth.create_account

import com.uk.ac.tees.mad.habitloop.domain.util.DataError

sealed interface CreateAccountEvent {
    data object Success : CreateAccountEvent
    data class Failure(val error: DataError) : CreateAccountEvent
    data object GoToLogin : CreateAccountEvent
}
