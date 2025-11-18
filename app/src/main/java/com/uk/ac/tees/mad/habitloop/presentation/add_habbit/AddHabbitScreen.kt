package com.uk.ac.tees.mad.habitloop.presentation.add_habbit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uk.ac.tees.mad.habitloop.presentation.common.BottomNavigationBar
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme

@Composable
fun AddHabbitRoot(
    viewModel: AddHabbitViewModel = viewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddHabbitScreen(
        state = state,
        onAction = viewModel::onAction,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabbitScreen(
    state: AddHabbitState,
    onAction: (AddHabbitAction) -> Unit,
    navController: NavHostController
) {
    if (state.isCustomFrequencyDialogVisible) {
        CustomFrequencyDialog(
            days = state.customFrequencyDays,
            selectedDays = state.selectedCustomFrequencyDays,
            onDaySelected = { day -> onAction(AddHabbitAction.OnCustomFrequencyDaySelected(day)) },
            onDismiss = { onAction(AddHabbitAction.OnCustomFrequencyDialogDismiss) }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Habit", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column {
                Button(
                    onClick = { onAction(AddHabbitAction.OnSaveClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save Habit", style = MaterialTheme.typography.titleMedium)
                }
                BottomNavigationBar(selectedTitle = "Add Habit", navController = navController)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Habit Title
            Section(title = "Habit Title") {
                OutlinedTextField(
                    value = state.habitTitle,
                    onValueChange = { onAction(AddHabbitAction.OnTitleChange(it)) },
                    placeholder = { Text("Meditate daily") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Category
            Section(title = "Category") {
                ChipGroup(
                    items = state.categories,
                    selectedItem = state.selectedCategory,
                    onItemSelected = { onAction(AddHabbitAction.OnCategoryChange(it)) }
                )
            }

            // Frequency
            Section(title = "Frequency") {
                ChipGroup(
                    items = state.frequencies,
                    selectedItem = state.selectedFrequency,
                    onItemSelected = { frequency ->
                        if (frequency == "Custom") {
                            onAction(AddHabbitAction.OnCustomFrequencyClick)
                        } else {
                            onAction(AddHabbitAction.OnFrequencyChange(frequency))
                        }
                    }
                )
            }

            // Reminder
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Enable Reminder", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Get notified at a specific time",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = state.isReminderEnabled,
                    onCheckedChange = { onAction(AddHabbitAction.OnReminderToggle(it)) }
                )
            }

            // Description
            Section(title = "Description") {
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { onAction(AddHabbitAction.OnDescriptionChange(it)) },
                    placeholder = { Text("Briefly describe your habit and its purpose...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipGroup(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            FilterChip(
                selected = item == selectedItem,
                onClick = { onItemSelected(item) },
                label = { Text(item) },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFrequencyDialog(
    days: List<String>,
    selectedDays: List<String>,
    onDaySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Days") },
        text = {
            Column {
                days.forEach { day ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedDays.contains(day),
                            onCheckedChange = { onDaySelected(day) }
                        )
                        Text(day)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    HabitLoopTheme {
        AddHabbitScreen(
            state = AddHabbitState(habitTitle = "Meditate daily"),
            onAction = {},
            navController = NavHostController(androidx.compose.ui.platform.LocalContext.current)
        )
    }
}
