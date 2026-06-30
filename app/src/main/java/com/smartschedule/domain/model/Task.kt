package com.smartschedule.domain.model

import com.smartschedule.data.db.entities.TaskEntity
import com.smartschedule.util.DateUtils

data class Task(
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val priority: Priority = Priority.MEDIUM,
    val targetDate: Long,
    val isCompleted: Boolean = false,
    val completionDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Long = 0
) {
    enum class Priority(val value: Int, val label: String) {
        LOW(0, "Low"),
        MEDIUM(1, "Medium"),
        HIGH(2, "High");

        companion object {
            fun fromValue(value: Int): Priority {
                return entries.firstOrNull { it.value == value } ?: MEDIUM
            }
        }
    }

    val formattedTargetDate: String
        get() = DateUtils.formatDateLabel(targetDate)

    val formattedCompletionDate: String
        get() = DateUtils.formatCompletionDate(completionDate)

    val daysUntilDue: Int
        get() = DateUtils.daysBetween(DateUtils.todayNormalized(), targetDate)

    val isOverdue: Boolean
        get() = !isCompleted && targetDate < DateUtils.todayNormalized()

    val overdueDays: Int
        get() = if (isOverdue) DateUtils.daysBetween(targetDate, DateUtils.todayNormalized()) else 0

    fun toEntity(): TaskEntity {
        return TaskEntity(
            id = id,
            title = title,
            notes = notes,
            priority = priority.value,
            targetDate = targetDate,
            isCompleted = isCompleted,
            completionDate = completionDate,
            createdAt = createdAt,
            sortOrder = sortOrder
        )
    }

    companion object {
        fun fromEntity(entity: TaskEntity): Task {
            return Task(
                id = entity.id,
                title = entity.title,
                notes = entity.notes,
                priority = Priority.fromValue(entity.priority),
                targetDate = entity.targetDate,
                isCompleted = entity.isCompleted,
                completionDate = entity.completionDate,
                createdAt = entity.createdAt,
                sortOrder = entity.sortOrder
            )
        }
    }
}

data class DateWithTasks(
    val date: Long,
    val tasks: List<Task>,
    val isToday: Boolean,
    val dateLabel: String
)

data class OverdueTask(
    val task: Task,
    val daysOverdue: Int
)
