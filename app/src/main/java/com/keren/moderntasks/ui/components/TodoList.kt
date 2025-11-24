package com.keren.moderntasks.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.keren.moderntasks.model.TodoItem

@Composable
fun TodoList(
    items: List<TodoItem>,
    onItemClick: (TodoItem) -> Unit,
    onItemCheckedChange: (TodoItem, Boolean) -> Unit,
    onDeleteClick: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(items = items, key = { it.id }) { item ->
            TodoItemRow(
                item = item,
                onItemClick = onItemClick,
                onCheckedChange = onItemCheckedChange,
                onDeleteClick = onDeleteClick
            )
        }
    }
}
