package com.smartschedule.data.db

import androidx.room.*
import com.smartschedule.data.db.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY sortOrder ASC, createdAt ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND targetDate = :date ORDER BY sortOrder ASC, createdAt ASC")
    fun getTasksForDate(date: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND targetDate < :today AND targetDate >= :cutoff ORDER BY targetDate ASC, sortOrder ASC")
    fun getOverdueTasks(today: Long, cutoff: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completionDate DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND targetDate BETWEEN :start AND :end ORDER BY targetDate ASC, sortOrder ASC")
    fun getUpcomingTasks(start: Long, end: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY sortOrder ASC")
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE priority = :priority AND isCompleted = 0 ORDER BY sortOrder ASC")
    fun getTasksByPriority(priority: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY sortOrder ASC, createdAt ASC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("DELETE FROM tasks WHERE isCompleted = 0 AND targetDate < :cutoffDate")
    suspend fun deleteOldOverdueTasks(cutoffDate: Long)

    @Query("UPDATE tasks SET sortOrder = :order WHERE id = :taskId")
    suspend fun updateSortOrder(taskId: Long, order: Long)

    @Query("UPDATE tasks SET isCompleted = 1, completionDate = :completionDate WHERE id = :taskId")
    suspend fun markTaskCompleted(taskId: Long, completionDate: Long)

    @Query("UPDATE tasks SET isCompleted = 0, completionDate = NULL WHERE id = :taskId")
    suspend fun markTaskIncomplete(taskId: Long)
}
