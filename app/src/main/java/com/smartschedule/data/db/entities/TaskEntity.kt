package com.smartschedule.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [
        Index("targetDate"),
        Index("isCompleted"),
        Index("priority"),
        Index("targetDate", "isCompleted")
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val priority: Int = 1,
    val targetDate: Long,
    val isCompleted: Boolean = false,
    val completionDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Long = 0
)
