package com.keren.moderntasks.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.keren.moderntasks.model.TodoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoList(
    items: List<TodoItem>,
    onItemClick: (TodoItem) -> Unit,
    onItemCheckedChange: (TodoItem, Boolean) -> Unit,
    onDeleteClick: (TodoItem) -> Unit,
    onReorder: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var targetIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        itemsIndexed(items = items, key = { _, item -> item.id }) { index, item ->
            val isDragging = draggedIndex == index
            val isTarget = targetIndex == index

            Box(
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer {
                        alpha = if (isDragging) 0.7f else 1f
                        scaleX = if (isTarget) 1.05f else 1f
                        scaleY = if (isTarget) 1.05f else 1f
                    }
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggedIndex = index
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                // Calculate target index based on drag position
                                val newTargetIndex = (change.position.y / size.height).toInt()
                                    .coerceIn(0, items.size - 1)
                                if (newTargetIndex != targetIndex) {
                                    targetIndex = newTargetIndex
                                }
                            },
                            onDragEnd = {
                                draggedIndex?.let { from ->
                                    targetIndex?.let { to ->
                                        if (from != to) {
                                            onReorder(from, to)
                                        }
                                    }
                                }
                                draggedIndex = null
                                targetIndex = null
                            },
                            onDragCancel = {
                                draggedIndex = null
                                targetIndex = null
                            }
                        )
                    }
            ) {
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                            onDeleteClick(item)
                            true
                        } else {
                            false
                        }
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        val color by animateColorAsState(
                            targetValue = when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.EndToStart, SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.surface
                            },
                            label = "swipeBackground"
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                            label = "iconScale"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.scale(scale),
                                tint = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                ) {
                    TodoItemRow(
                        item = item,
                        onItemClick = onItemClick,
                        onCheckedChange = onItemCheckedChange,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}
