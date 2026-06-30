package com.smartschedule.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartschedule.data.db.AppDatabase
import com.smartschedule.data.repository.TaskRepository
import com.smartschedule.domain.model.DateWithTasks
import com.smartschedule.domain.model.OverdueTask
import com.smartschedule.domain.model.Task
import com.smartschedule.domain.repository.TaskRepository as TaskRepositoryInterface
import com.smartschedule.util.Constants
import com.smartschedule.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepositoryInterface

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _priorityFilter = MutableStateFlow(-1)
    val priorityFilter: StateFlow<Int> = _priorityFilter.asStateFlow()

    private val _dateExpanded = MutableStateFlow<Set<Long>>(emptySet())
    val dateExpanded: StateFlow<Set<Long>> = _dateExpanded.asStateFlow()

    val today: Long = DateUtils.todayNormalized()

    private val allTasks: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao())
        repository.getAllTasks()
            .onEach { allTasks.value = it }
            .launchIn(viewModelScope)
        cleanupOverdueTasks()
    }

    val datesWithTasks: StateFlow<List<DateWithTasks>> = allTasks.map { tasks ->
        val now = DateUtils.todayNormalized()
        DateUtils.getNextNDays(Constants.DAYS_TO_SHOW).map { date ->
            DateWithTasks(
                date = date,
                tasks = tasks.filter { t ->
                    !t.isCompleted && DateUtils.normalizeDate(t.targetDate) == date
                },
                isToday = date == now,
                dateLabel = DateUtils.formatDateLabel(date)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val overdueTasks: StateFlow<List<OverdueTask>> = allTasks.map { tasks ->
        val now = DateUtils.todayNormalized()
        val cutoff = DateUtils.getThreeDaysAgoNormalized()
        tasks.filter { t ->
            !t.isCompleted && DateUtils.normalizeDate(t.targetDate) < now
            && DateUtils.normalizeDate(t.targetDate) >= cutoff
        }.map { t ->
            OverdueTask(
                task = t,
                daysOverdue = DateUtils.daysBetween(t.targetDate, now)
            )
        }.sortedByDescending { it.daysOverdue }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTasks: StateFlow<List<Task>> = allTasks.map { tasks ->
        tasks.filter { it.isCompleted }
            .sortedByDescending { it.completionDate ?: 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTasks: StateFlow<List<Task>> = combine(
        allTasks,
        searchQuery,
        priorityFilter,
        isSearchActive
    ) { tasks, query, priority, searchActive ->
        tasks.filter { task ->
            val matchesSearch = !searchActive || query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.notes.contains(query, ignoreCase = true)
            val matchesPriority = priority < 0 || task.priority.value == priority
            matchesSearch && matchesPriority
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank() && !_isSearchActive.value) return
        _isSearchActive.value = query.isNotBlank()
    }

    fun toggleSearch() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) {
            _searchQuery.value = ""
            _priorityFilter.value = -1
        }
    }

    fun setPriorityFilter(priority: Int) {
        _priorityFilter.value = if (_priorityFilter.value == priority) -1 else priority
    }

    fun toggleDateExpanded(date: Long) {
        _dateExpanded.update { current ->
            current.toMutableSet().apply {
                if (contains(date)) remove(date) else add(date)
            }
        }
    }

    fun addTask(title: String, notes: String, priority: Int, targetDate: Long) {
        viewModelScope.launch {
            repository.insertTask(
                Task(
                    title = title.trim(),
                    notes = notes.trim(),
                    priority = Task.Priority.fromValue(priority),
                    targetDate = DateUtils.normalizeDate(targetDate),
                    sortOrder = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(targetDate = DateUtils.normalizeDate(task.targetDate)))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            if (task.isCompleted) {
                repository.markTaskIncomplete(task.id)
            } else {
                repository.markTaskCompleted(task.id, System.currentTimeMillis())
            }
        }
    }

    fun moveTaskToDate(task: Task, newDate: Long) {
        viewModelScope.launch {
            repository.updateTask(task.copy(targetDate = DateUtils.normalizeDate(newDate)))
        }
    }

    fun updateSortOrder(taskId: Long, order: Long) {
        viewModelScope.launch {
            repository.updateSortOrder(taskId, order)
        }
    }

    fun swapTaskOrder(tasks: List<Task>, fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        val mutableTasks = tasks.toMutableList()
        val moved = mutableTasks.removeAt(fromIndex)
        mutableTasks.add(toIndex, moved)
        viewModelScope.launch {
            mutableTasks.forEachIndexed { index, task ->
                repository.updateSortOrder(task.id, index.toLong())
            }
        }
    }

    private fun cleanupOverdueTasks() {
        viewModelScope.launch {
            repository.deleteOldOverdueTasks(DateUtils.getThreeDaysAgoNormalized())
        }
    }
}
