package com.uk.ac.tees.mad.habitloop.presentation.setting

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.uk.ac.tees.mad.habitloop.presentation.common.BottomNavigationBar
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingRoot(
    viewModel: SettingViewModel = koinViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    SettingScreen(
        state = state,
        onAction = viewModel::onAction,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    state: SettingState,
    onAction: (SettingAction) -> Unit,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedTitle = "Settings", navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Section(title = "Notification Preferences") {
                ToggleRow(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    title = "Notification Sound",
                    isChecked = state.isNotificationSoundOn,
                    onToggle = { onAction(SettingAction.OnNotificationSoundToggle(it)) }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                ToggleRow(
                    icon = Icons.Default.Vibration,
                    title = "Notification Vibration",
                    isChecked = state.isNotificationVibrationOn,
                    onToggle = { onAction(SettingAction.OnNotificationVibrationToggle(it)) }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                ClickableRow(
                    icon = Icons.Default.CalendarToday,
                    title = "Notification Frequency",
                    value = state.notificationFrequency,
                    onClick = { onAction(SettingAction.OnNotificationFrequencyClick) }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                ToggleRow(
                    icon = Icons.Default.FormatQuote,
                    title = "Daily Quote Notifications",
                    subtitle = "Receive motivational quotes daily",
                    isChecked = state.isDailyQuoteNotificationsOn,
                    onToggle = { onAction(SettingAction.OnDailyQuoteNotificationsToggle(it)) }
                )
            }

            Section(title = "Data Management") {
                ClickableRow(
                    icon = Icons.Default.Backup,
                    title = "Backup Data",
                    onClick = { onAction(SettingAction.OnBackupDataClick) }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                ClickableRow(
                    icon = Icons.Default.Restore,
                    title = "Restore Data",
                    onClick = { onAction(SettingAction.OnRestoreDataClick) }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                ClickableRow(
                    icon = Icons.Default.Delete,
                    title = "Clear Cache",
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { onAction(SettingAction.OnClearCacheClick) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        Switch(checked = isChecked, onCheckedChange = onToggle)
    }
}

@Composable
fun ClickableRow(
    icon: ImageVector,
    title: String,
    value: String? = null,
    titleColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = if (titleColor != Color.Unspecified) titleColor else LocalContentColor.current
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = titleColor
        )
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    HabitLoopTheme {
        SettingScreen(
            state = SettingState(),
            onAction = {},
            navController = rememberNavController()
        )
    }
}
