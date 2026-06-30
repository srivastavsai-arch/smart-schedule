package com.smartschedule.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smartschedule.domain.model.Task
import com.smartschedule.ui.theme.*
import com.smartschedule.util.DateUtils
import java.util.*

@Composable
fun AddEditTaskDialog(
    existingTask: Task? = null,
    targetDate: Long,
    onDismiss: () -> Unit,
    onSave: (title: String, notes: String, priority: Int, targetDate: Long) -> Unit
) {
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var notes by remember { mutableStateOf(existingTask?.notes ?: "") }
    var priority by remember { mutableIntStateOf(existingTask?.priority?.value ?: 1) }
    var titleError by remember { mutableStateOf(false) }

    val isEditing = existingTask != null
    val dateLabel = DateUtils.formatDateLabel(targetDate)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (isEditing) "Edit Task" else "New Task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text("Task title") },
                    placeholder = { Text("What needs to be done?") },
                    isError = titleError,
                    supportingText = if (titleError) {{ Text("Title is required") }} else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    placeholder = { Text("Add details...") },
                    minLines = 2,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Task.Priority.entries.forEach { p ->
                        val selected = priority == p.value
                        val chipColor = when (p) {
                            Task.Priority.LOW -> PriorityLow
                            Task.Priority.MEDIUM -> PriorityMedium
                            Task.Priority.HIGH -> PriorityHigh
                        }
                        val chipBg = when (p) {
                            Task.Priority.LOW -> PriorityLowBg
                            Task.Priority.MEDIUM -> PriorityMediumBg
                            Task.Priority.HIGH -> PriorityHighBg
                        }

                        FilterChip(
                            selected = selected,
                            onClick = { priority = p.value },
                            label = {
                                Text(
                                    p.label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Flag,
                                    contentDescription = null,
                                    tint = if (selected) chipColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (selected) chipBg else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                selectedContainerColor = chipBg
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = true
                            } else {
                                onSave(title, notes, priority, targetDate)
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isEditing) "Update" else "Add Task")
                    }
                }
            }
        }
    }
}
