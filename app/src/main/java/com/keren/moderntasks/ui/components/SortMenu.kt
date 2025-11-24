package com.keren.moderntasks.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.keren.moderntasks.ui.SortOrder

@Composable
fun SortMenu(
    currentSort: SortOrder,
    onSortSelected: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Sort options"
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Sort by Date") },
            onClick = {
                onSortSelected(SortOrder.BY_DATE)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text("Sort Alphabetically") },
            onClick = {
                onSortSelected(SortOrder.ALPHABETICAL)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text("Sort by Completion") },
            onClick = {
                onSortSelected(SortOrder.BY_COMPLETION)
                expanded = false
            }
        )
    }
}
