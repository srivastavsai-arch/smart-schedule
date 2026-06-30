package com.smartschedule.data.repository

import com.smartschedule.data.db.TaskDao
import com.smartschedule.domain.model.Task
import com.smartschedule.domain.repository.TaskRepository as TaskRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) : TaskRepositoryInterface {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities -> entities.map { Task.fromEntity(it) } }
    }

    override fun getActiveTasks(): Flow<List<Task>> {
        return taskDao.getActiveTasks().map { entities -> entities.map { Task.fromEntity(it) } }
    }

    override fun getTasksForDate(date: Long): Flow<List<Task>> {
        return taskDao.getTasksForDate(date).map { entities -> entities.map { Task.fromEntity(it) } }
    }

    override fun getOverdueTasks(today: Long, cutoff: Long): Flow<List<Task>> {
        return taskDao.getOverdueTasks(today, cutoff).map { entities -> entities.map { Task.fromEntity(it) } }
    }

    override fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks().map { entities -> entities.map { Task.fromEntity(it) } }
    }

    override fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasks(query).map { entities -> entities.map { Task.fromEntity(it) } }
    }

    override fun getTasksByPriority(priority: Int): Flow<List<Task>> {
        return taskDao.getTasksByPriority(priority).map { entities -> entities.map { Task.fromEntity(it) } }
    }

    override fun getUpcomingTasks(start: Long, end: Long): Flow<List<Task>> {
        return taskDao.getUpcomingTasks(start, end).map { entities -> entities.map { Task.fromEntity(it) } }
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)?.let { Task.fromEntity(it) }
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override suspend fun deleteTaskById(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    override suspend fun deleteOldOverdueTasks(cutoffDate: Long) {
        taskDao.deleteOldOverdueTasks(cutoffDate)
    }

    override suspend fun updateSortOrder(taskId: Long, order: Long) {
        taskDao.updateSortOrder(taskId, order)
    }

    override suspend fun markTaskCompleted(taskId: Long, completionDate: Long) {
        taskDao.markTaskCompleted(taskId, completionDate)
    }

    override suspend fun markTaskIncomplete(taskId: Long) {
        taskDao.markTaskIncomplete(taskId)
    }
}
