package com.uk.ac.tees.mad.habitloop.presentation.forgot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme

@Composable
fun ForgotRoot(
    viewModel: ForgotViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ForgotScreen(
    state: ForgotState,
    onAction: (ForgotAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    HabitLoopTheme {
        ForgotScreen(
            state = ForgotState(),
            onAction = {}
        )
    }
}