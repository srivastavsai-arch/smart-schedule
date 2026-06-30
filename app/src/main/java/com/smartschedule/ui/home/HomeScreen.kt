package com.smartschedule.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartschedule.domain.model.Task
import com.smartschedule.ui.components.*
import com.smartschedule.util.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onRequestNotificationPermission: () -> Unit
) {
    val datesWithTasks by viewModel.datesWithTasks.collectAsState()
    val overdueTasks by viewModel.overdueTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val priorityFilter by viewModel.priorityFilter.collectAsState()
    val dateExpanded by viewModel.dateExpanded.collectAsState()
    val filteredTasks by viewModel.filteredTasks.collectAsState()
    val today = viewModel.today

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showEditTaskDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var selectedDateForAdd by remember { mutableStateOf(today) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val topBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Smart Schedule",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = DateUtils.formatShortDate(today),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRequestNotificationPermission) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                scrollBehavior = topBarScrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    selectedDateForAdd = today
                    showAddTaskDialog = true
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Task")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SearchAndFilterBar(
                    searchQuery = searchQuery,
                    isSearchActive = isSearchActive,
                    priorityFilter = priorityFilter,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onToggleSearch = { viewModel.toggleSearch() },
                    onPriorityFilter = { viewModel.setPriorityFilter(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isSearchActive || priorityFilter >= 0) {
                if (filteredTasks.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No matching tasks",
                            subtitle = "Try a different search or filter",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onEdit = { selectedTask = task; showEditTaskDialog = true },
                            onDelete = { selectedTask = task; showDeleteDialog = true },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                items(datesWithTasks, key = { it.date }) { dateWithTasks ->
                    DateCard(
                        dateWithTasks = dateWithTasks,
                        isExpanded = dateExpanded.contains(dateWithTasks.date),
                        onToggleExpand = { viewModel.toggleDateExpanded(dateWithTasks.date) },
                        onAddTask = {
                            selectedDateForAdd = dateWithTasks.date
                            showAddTaskDialog = true
                        },
                        onToggleComplete = { task ->
                            viewModel.toggleTaskCompletion(task)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = if (!task.isCompleted) "Task completed" else "Task uncompleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        onEditTask = { task -> selectedTask = task; showEditTaskDialog = true },
                        onDeleteTask = { task -> selectedTask = task; showDeleteDialog = true },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                if (overdueTasks.isNotEmpty()) {
                    item {
                        OverdueSection(
                            overdueTasks = overdueTasks,
                            onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                            onEditTask = { selectedTask = it; showEditTaskDialog = true },
                            onDeleteTask = { selectedTask = it; showDeleteDialog = true },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                if (completedTasks.isNotEmpty()) {
                    item {
                        CompletedSection(
                            tasks = completedTasks,
                            onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                            onDeleteTask = { selectedTask = it; showDeleteDialog = true },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                if (datesWithTasks.all { it.tasks.isEmpty() } && overdueTasks.isEmpty() && completedTasks.isEmpty()) {
                    item { EmptyState(modifier = Modifier.padding(horizontal = 16.dp)) }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAddTaskDialog) {
        AddEditTaskDialog(
            targetDate = selectedDateForAdd,
            onDismiss = { showAddTaskDialog = false },
            onSave = { title, notes, priority, date ->
                viewModel.addTask(title, notes, priority, date)
                showAddTaskDialog = false
            }
        )
    }

    if (showEditTaskDialog && selectedTask != null) {
        AddEditTaskDialog(
            existingTask = selectedTask,
            targetDate = selectedTask!!.targetDate,
            onDismiss = {
                showEditTaskDialog = false
                selectedTask = null
            },
            onSave = { title, notes, priority, date ->
                viewModel.updateTask(
                    selectedTask!!.copy(
                        title = title,
                        notes = notes,
                        priority = Task.Priority.fromValue(priority),
                        targetDate = date
                    )
                )
                showEditTaskDialog = false
                selectedTask = null
            }
        )
    }

    if (showDeleteDialog && selectedTask != null) {
        ConfirmDeleteDialog(
            taskTitle = selectedTask!!.title,
            onDismiss = {
                showDeleteDialog = false
                selectedTask = null
            },
            onConfirm = {
                viewModel.deleteTask(selectedTask!!)
                showDeleteDialog = false
                selectedTask = null
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Task deleted",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }
}
