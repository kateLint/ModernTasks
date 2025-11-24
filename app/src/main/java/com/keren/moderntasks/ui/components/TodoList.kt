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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.keren.moderntasks.model.TodoItem
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoList(
    items: List<TodoItem>,
    onItemClick: (TodoItem) -> Unit,
    onItemCheckedChange: (TodoItem, Boolean) -> Unit,
    onDeleteClick: (TodoItem) -> Unit,
    onReorderFinished: (List<TodoItem>) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val localItems = remember { mutableStateListOf<TodoItem>() }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // Sync local items with source of truth when not dragging
    LaunchedEffect(items) {
        if (draggedIndex == null) {
            localItems.clear()
            localItems.addAll(items)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        itemsIndexed(items = localItems, key = { _, item -> item.id }) { index, item ->
            val isDragging = draggedIndex == index
            val displacement = if (isDragging) dragOffset else 0f
            val currentIndex by rememberUpdatedState(index)
            val currentOnReorderFinished by rememberUpdatedState(onReorderFinished)

            Box(
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer {
                        translationY = displacement
                        if (isDragging) {
                            scaleX = 1.05f
                            scaleY = 1.05f
                            alpha = 0.9f
                            shadowElevation = 8.dp.toPx()
                        }
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
                        onDeleteClick = onDeleteClick,
                        dragHandleModifier = Modifier.pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    draggedIndex = currentIndex
                                    dragOffset = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffset += dragAmount.y
                                    
                                    val currentDraggedIndex = draggedIndex ?: return@detectDragGesturesAfterLongPress
                                    val itemHeight = 80.dp.toPx() // Approximate item height
                                    
                                    // Calculate target index
                                    val targetIndex = (currentDraggedIndex + (dragOffset / itemHeight).roundToInt())
                                        .coerceIn(0, localItems.size - 1)
                                        
                                    if (targetIndex != currentDraggedIndex) {
                                        // Swap items in local list
                                        val movedItem = localItems.removeAt(currentDraggedIndex)
                                        localItems.add(targetIndex, movedItem)
                                        
                                        // Update state
                                        draggedIndex = targetIndex
                                        // Adjust offset to account for the swap so the item stays under the finger
                                        dragOffset -= (targetIndex - currentDraggedIndex) * itemHeight
                                    }
                                },
                                onDragEnd = {
                                    draggedIndex = null
                                    dragOffset = 0f
                                    currentOnReorderFinished(localItems.toList())
                                },
                                onDragCancel = {
                                    draggedIndex = null
                                    dragOffset = 0f
                                    // Revert to original items
                                    localItems.clear()
                                    localItems.addAll(items)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
