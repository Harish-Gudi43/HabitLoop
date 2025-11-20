package com.uk.ac.tees.mad.habitloop.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.uk.ac.tees.mad.habitloop.R
import com.uk.ac.tees.mad.habitloop.domain.models.Habit
import com.uk.ac.tees.mad.habitloop.domain.util.NavigationEvent
import com.uk.ac.tees.mad.habitloop.domain.util.ObserveAsEvents
import com.uk.ac.tees.mad.habitloop.presentation.common.BottomNavigationBar
import com.uk.ac.tees.mad.habitloop.presentation.navigation.GraphRoutes
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardRoot(
    viewModel: DashboardViewModel = koinViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navigationEvent) {
        when (it) {
            is NavigationEvent.NavigateToEditHabit -> {
                navController.navigate(GraphRoutes.AddHabbit(it.habitId))
            }
            else -> Unit
        }
    }

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile_placeholder),
                            contentDescription = "Notifications"
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile_placeholder),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedTitle = "Dashboard", navController = navController)
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(DashboardAction.OnRefresh) },
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Good morning, ${state.userName}!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                QuoteCard(quote = state.quote, author = state.quoteAuthor)
                ViewToggle(
                    isGridView = state.isGridView,
                    onToggle = { isGridView -> onAction(DashboardAction.OnViewToggle(isGridView)) }
                )

                if (state.habits.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No habits yet. Add one to get started!")
                    }
                } else {
                    if (state.isGridView) {
                        HabitGrid(habits = state.habits, onAction = onAction)
                    } else {
                        HabitList(habits = state.habits, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteCard(quote: String, author: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            if (quote.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Text(
                    text = "”",
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-40).dp)
                )
                Column {
                    Text(
                        text = quote,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(end = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "- $author",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ViewToggle(isGridView: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text("List View", color = if (!isGridView) MaterialTheme.colorScheme.primary else Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isGridView,
            onCheckedChange = onToggle
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Grid View", color = if (isGridView) MaterialTheme.colorScheme.primary else Color.Gray)
    }
}

@Composable
fun HabitGrid(habits: List<Habit>, onAction: (DashboardAction) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(habits, key = { "grid-${it.id}" }) { habit ->
            HabitGridItem(habit = habit, onAction = onAction)
        }
    }
}

@Composable
fun HabitList(habits: List<Habit>, onAction: (DashboardAction) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(habits, key = { "list-${it.id}" }) { habit ->
            HabitListItem(habit = habit, onAction = onAction)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitGridItem(habit: Habit, onAction: (DashboardAction) -> Unit) {
    val cardColors = if (habit.isCompleted) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = cardColors
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = habit.isCompleted,
                    onCheckedChange = { onAction(DashboardAction.OnHabitClick(habit.id)) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = habit.name,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (habit.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onAction(DashboardAction.OnEditClick(habit.id)) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Habit")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fire),
                    contentDescription = "Streak",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${habit.streak} Days",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Next: ${habit.nextOccurrence}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListItem(habit: Habit, onAction: (DashboardAction) -> Unit) {
    val cardColors = if (habit.isCompleted) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = cardColors
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = habit.isCompleted,
                    onCheckedChange = { onAction(DashboardAction.OnHabitClick(habit.id)) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = habit.name,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (habit.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_fire),
                            contentDescription = "Streak",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${habit.streak} Days",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Next: ${habit.nextOccurrence}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                IconButton(onClick = { onAction(DashboardAction.OnEditClick(habit.id)) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Habit")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "List View Preview")
@Composable
private fun PreviewListView() {
    HabitLoopTheme {
        DashboardScreen(
            state = DashboardState(
                isGridView = false, // Explicitly set to list view for this preview
                habits = listOf(
                    Habit(id = "1", name = "Morning Run", description = "", category = "", frequency = "", reminder = false, customFrequencyDays = null, isCompleted = true, streak = 15, nextOccurrence = "6:00 AM"),
                    Habit(id = "2", name = "Read Book", description = "", category = "", frequency = "", reminder = false, customFrequencyDays = null, isCompleted = false, streak = 7, nextOccurrence = "9:00 PM"),
                )
            ),
            onAction = {},
            navController = NavHostController(LocalContext.current)
        )
    }
}

@Preview(showBackground = true, name = "Grid View Preview")
@Composable
private fun PreviewGridView() {
    HabitLoopTheme {
        DashboardScreen(
            state = DashboardState(
                isGridView = true, // Explicitly set to grid view for this preview
                habits = listOf(
                    Habit(id = "1", name = "Morning Run", description = "", category = "", frequency = "", reminder = false, customFrequencyDays = null, isCompleted = true, streak = 15, nextOccurrence = "6:00 AM"),
                    Habit(id = "2", name = "Read Book", description = "", category = "", frequency = "", reminder = false, customFrequencyDays = null, isCompleted = false, streak = 7, nextOccurrence = "9:00 PM"),
                    Habit(id = "3", name = "Drink Water", description = "", category = "", frequency = "", reminder = false, customFrequencyDays = null, isCompleted = true, streak = 30, nextOccurrence = "2:00 PM"),
                    Habit(id = "4", name = "Meditate", description = "", category = "", frequency = "", reminder = false, customFrequencyDays = null, isCompleted = false, streak = 3, nextOccurrence = "7:00 AM"),
                )
            ),
            onAction = {},
            navController = NavHostController(LocalContext.current)
        )
    }
}
