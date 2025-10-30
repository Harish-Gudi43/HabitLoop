package com.uk.ac.tees.mad.habitloop.presentation.create_account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme

@Composable
fun CreateAccountRoot(
    viewModel: CreateAccountViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CreateAccountScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun CreateAccountScreen(
    state: CreateAccountState,
    onAction: (CreateAccountAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    HabitLoopTheme {
        CreateAccountScreen(
            state = CreateAccountState(),
            onAction = {}
        )
    }
}