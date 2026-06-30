package com.smartschedule.domain.repository

import com.smartschedule.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getActiveTasks(): Flow<List<Task>>
    fun getTasksForDate(date: Long): Flow<List<Task>>
    fun getOverdueTasks(today: Long, cutoff: Long): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>
    fun getTasksByPriority(priority: Int): Flow<List<Task>>
    fun getUpcomingTasks(start: Long, end: Long): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(taskId: Long)
    suspend fun deleteOldOverdueTasks(cutoffDate: Long)
    suspend fun updateSortOrder(taskId: Long, order: Long)
    suspend fun markTaskCompleted(taskId: Long, completionDate: Long)
    suspend fun markTaskIncomplete(taskId: Long)
}
