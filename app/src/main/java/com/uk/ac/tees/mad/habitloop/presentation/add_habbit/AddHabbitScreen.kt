package com.uk.ac.tees.mad.habitloop.presentation.add_habbit

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.uk.ac.tees.mad.habitloop.domain.util.NavigationEvent
import com.uk.ac.tees.mad.habitloop.domain.util.ObserveAsEvents
import com.uk.ac.tees.mad.habitloop.presentation.common.BottomNavigationBar
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun AddHabbitRoot(
    viewModel: AddHabbitViewModel = koinViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigationEvent) {
        when (it) {
            is NavigationEvent.NavigateBack -> {
                navController.popBackStack()
            }
            else -> Unit
        }
    }

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
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onAction(AddHabbitAction.OnReminderToggle(true))
            }
        }
    )

    if (state.isTimePickerVisible) {
        TimePickerDialog(
            context = context,
            onTimeSet = { hour, minute ->
                onAction(AddHabbitAction.OnTimeSelected(hour, minute))
            },
            onCancel = { onAction(AddHabbitAction.OnTimePickerDismiss) },
            initialHour = state.reminderHour ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            initialMinute = state.reminderMinute ?: Calendar.getInstance().get(Calendar.MINUTE)
        )
    }

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
                    onCheckedChange = { isEnabled ->
                        if (isEnabled) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                                    PackageManager.PERMISSION_GRANTED -> {
                                        onAction(AddHabbitAction.OnReminderToggle(true))
                                    }
                                    else -> {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            } else {
                                onAction(AddHabbitAction.OnReminderToggle(true))
                            }
                        } else {
                            onAction(AddHabbitAction.OnReminderToggle(false))
                        }
                    }
                )
            }

            if (state.isReminderEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(AddHabbitAction.OnTimePickerClick) }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Reminder Time", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${state.reminderHour ?: "--"}:${state.reminderMinute ?: "--"}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
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
fun TimePickerDialog(
    context: Context,
    onTimeSet: (hour: Int, minute: Int) -> Unit,
    onCancel: () -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute -> onTimeSet(hour, minute) },
        initialHour, initialMinute, false
    )
    timePickerDialog.setOnCancelListener { onCancel() }
    timePickerDialog.show()
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
            navController = NavHostController(LocalContext.current)
        )
    }
}
