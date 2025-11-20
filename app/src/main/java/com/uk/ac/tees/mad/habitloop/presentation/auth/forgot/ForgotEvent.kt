package com.uk.ac.tees.mad.habitloop.presentation.auth.forgot

import com.uk.ac.tees.mad.habitloop.domain.util.DataError

sealed interface ForgotEvent {
    data object Success : ForgotEvent
    data class Failure(val error: DataError) : ForgotEvent
    data object GoToLogin : ForgotEvent
}
