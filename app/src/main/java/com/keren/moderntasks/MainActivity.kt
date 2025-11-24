package com.keren.moderntasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.keren.moderntasks.data.TodoDatabase
import com.keren.moderntasks.data.TodoRepository
import com.keren.moderntasks.ui.TodoViewModel
import com.keren.moderntasks.model.TodoItem
import com.keren.moderntasks.ui.components.AddTodoDialog
import com.keren.moderntasks.ui.components.AddTodoFab
import com.keren.moderntasks.ui.components.EmptyState
import com.keren.moderntasks.ui.components.SortMenu
import com.keren.moderntasks.ui.components.TodoList
import com.keren.moderntasks.ui.theme.ModernTasksTheme

import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        


        setContent {
            ModernTasksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ModernTasksApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTasksApp() {
    val viewModel: TodoViewModel = hiltViewModel()
    val todoItems by viewModel.todoItems.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<TodoItem?>(null) }

    if (showDialog) {
        AddTodoDialog(
            item = editingItem,
            onDismiss = { 
                showDialog = false 
                editingItem = null
            },
            onConfirm = { title, description ->
                if (editingItem == null) {
                    viewModel.addTodo(title, description)
                } else {
                    viewModel.updateTodo(editingItem!!.copy(title = title, description = description))
                }
                showDialog = false
                editingItem = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modern Tasks") },
                actions = {
                    SortMenu(
                        currentSort = sortOrder,
                        onSortSelected = { viewModel.setSortOrder(it) }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            AddTodoFab(onClick = {
                editingItem = null
                showDialog = true
            })
        }
    ) { innerPadding ->
        if (todoItems.isEmpty()) {
            EmptyState(modifier = Modifier.padding(innerPadding))
        } else {
            TodoList(
                items = todoItems,
                onItemClick = { item ->
                    editingItem = item
                    showDialog = true
                },
                onItemCheckedChange = { item, _ ->
                    viewModel.toggleCompletion(item)
                },
                onDeleteClick = { item ->
                    viewModel.deleteTodo(item)
                },
                onReorder = { fromIndex, toIndex ->
                    viewModel.reorderTasks(fromIndex, toIndex)
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
