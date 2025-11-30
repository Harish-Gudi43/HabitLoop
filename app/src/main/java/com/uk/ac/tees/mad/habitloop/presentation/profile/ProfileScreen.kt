package com.uk.ac.tees.mad.habitloop.presentation.profile

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.uk.ac.tees.mad.habitloop.R
import com.uk.ac.tees.mad.habitloop.domain.util.NavigationEvent
import com.uk.ac.tees.mad.habitloop.domain.util.ObserveAsEvents
import com.uk.ac.tees.mad.habitloop.presentation.common.BottomNavigationBar
import com.uk.ac.tees.mad.habitloop.presentation.navigation.GraphRoutes
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileRoot(
    viewModel: ProfileViewModel = koinViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navigationEvent) {
        if (it is NavigationEvent.NavigateToLogin) {
            navController.navigate(GraphRoutes.Login) {
                popUpTo(GraphRoutes.Profile) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    ProfileScreen(
        state = state,
        onAction = viewModel::onAction,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    navController: NavHostController
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { onAction(ProfileAction.OnProfileImageChange(it)) } }
    )
    val readImagesPermission = rememberPermissionState(
        permission = Manifest.permission.READ_MEDIA_IMAGES
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile & Stats", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedTitle = "Profile", navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            ProfileHeader(
                state = state,
                onAction = onAction,
                onImageClick = {
                    if (readImagesPermission.status.isGranted) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    } else {
                        readImagesPermission.launchPermissionRequest()
                    }
                }
            )
            Spacer(Modifier.height(24.dp))
            StatsGrid(state = state)
            Spacer(Modifier.height(24.dp))
            WeeklyProgressCard(state.weeklyProgress)
            Spacer(Modifier.height(24.dp))
            SettingsToggles(state = state, onAction = onAction)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onAction(ProfileAction.OnLogoutClick) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileHeader(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = state.profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { onImageClick() },
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_profile_placeholder)
                )
                if (state.isUploadingPhoto) {
                    CircularProgressIndicator()
                }
            }
            Text(text = state.userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            TextButton(onClick = { onAction(ProfileAction.OnEditProfileClick) }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Icon", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Edit Profile", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun StatsGrid(state: ProfileState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                icon = Icons.Default.CalendarToday,
                label = "Total Habits",
                value = state.totalHabits.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.LocalFireDepartment,
                label = "Current Streak",
                value = "${state.currentStreak} Days",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                icon = Icons.Default.TrendingUp,
                label = "Longest Streak",
                value = "${state.longestStreak} Days",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.CheckCircleOutline,
                label = "Completion Rate",
                value = "${state.completionRate}%",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WeeklyProgressCard(progressData: List<WeeklyProgress>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weekly Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            BarChart(progressData)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                Box(modifier = Modifier.size(10.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Completion", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun BarChart(data: List<WeeklyProgress>) {
    val barColor = MaterialTheme.colorScheme.primary
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        if (data.isEmpty()) return@Canvas

        val barSpacing = 32.dp.toPx()
        val totalSpacing = (data.size - 1) * barSpacing
        val barWidth = (size.width - totalSpacing) / data.size

        data.forEachIndexed { index, progress ->
            val barHeight = size.height * progress.completionPercentage
            val startX = index * (barWidth + barSpacing)

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x = startX, y = size.height - barHeight),
                size = Size(width = barWidth, height = barHeight),
                cornerRadius = CornerRadius(8.dp.toPx())
            )
        }
    }
}

@Composable
fun SettingsToggles(state: ProfileState, onAction: (ProfileAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ToggleItem(
            title = "Motivation Mode",
            subtitle = "Receive inspiring quotes and reminders.",
            isChecked = state.isMotivationModeOn,
            onCheckedChange = { onAction(ProfileAction.OnMotivationModeToggle(it)) }
        )
        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
        ToggleItem(
            title = "Theme Switch",
            subtitle = "Toggle between Light and Dark themes.",
            isChecked = state.isDarkModeOn,
            onCheckedChange = { onAction(ProfileAction.OnThemeSwitchToggle(it)) }
        )
        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
        ToggleItem(
            title = "Notifications",
            subtitle = "Enable or disable all notifications.",
            isChecked = state.isNotificationsEnabled,
            onCheckedChange = { onAction(ProfileAction.OnNotificationsToggle(it)) }
        )
    }
}

@Composable
fun ToggleItem(title: String, subtitle: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreen() {
    HabitLoopTheme {
        // We pass a dummy NavController for the preview to work
        val navController = rememberNavController()
        ProfileScreen(
            state = ProfileState(
                profileImageUrl = null,
                weeklyProgress = listOf(
                    WeeklyProgress("Week 1", 0.70f),
                    WeeklyProgress("Week 2", 0.85f),
                    WeeklyProgress("Week 3", 0.75f),
                    WeeklyProgress("Week 4", 0.90f)
                )
            ),
            onAction = {},
            navController = navController
        )
    }
}
