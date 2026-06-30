package com.smartschedule.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartschedule.domain.model.Task
import com.smartschedule.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterBar(
    searchQuery: String,
    isSearchActive: Boolean,
    priorityFilter: Int,
    onSearchQueryChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onPriorityFilter: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search tasks") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        IconButton(onClick = onToggleSearch) {
                            Icon(Icons.Filled.Close, contentDescription = "Close search")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onToggleSearch) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filter by priority",
                            tint = if (priorityFilter >= 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "All Priorities",
                                    fontWeight = if (priorityFilter < 0) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onPriorityFilter(-1)
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.FilterList,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                        HorizontalDivider()
                        Task.Priority.entries.forEach { p ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        p.label,
                                        fontWeight = if (priorityFilter == p.value) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onPriorityFilter(p.value)
                                    showFilterMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Flag,
                                        contentDescription = null,
                                        tint = when (p) {
                                            Task.Priority.LOW -> PriorityLow
                                            Task.Priority.MEDIUM -> PriorityMedium
                                            Task.Priority.HIGH -> PriorityHigh
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = priorityFilter >= 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Task.Priority.entries.filter { it.value == priorityFilter }.forEach { p ->
                    val chipColor = when (p) {
                        Task.Priority.LOW -> PriorityLow
                        Task.Priority.MEDIUM -> PriorityMedium
                        Task.Priority.HIGH -> PriorityHigh
                    }
                    FilterChip(
                        selected = true,
                        onClick = { onPriorityFilter(p.value) },
                        label = {
                            Text(
                                p.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Flag,
                                contentDescription = null,
                                tint = chipColor,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor.copy(alpha = 0.15f)
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { onPriorityFilter(-1) },
                                modifier = Modifier.size(14.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Clear filter",
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
