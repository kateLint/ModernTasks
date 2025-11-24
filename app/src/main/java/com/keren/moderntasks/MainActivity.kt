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
import com.keren.moderntasks.ui.components.AddTodoDialog
import com.keren.moderntasks.ui.components.AddTodoFab
import com.keren.moderntasks.ui.components.TodoList
import com.keren.moderntasks.ui.theme.ModernTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java, "todo-database"
        ).build()
        
        val repository = TodoRepository(db.todoDao())
        
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return TodoViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            ModernTasksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ModernTasksApp(viewModelFactory = viewModelFactory)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTasksApp(viewModelFactory: ViewModelProvider.Factory) {
    val viewModel: TodoViewModel = viewModel(factory = viewModelFactory)
    val todoItems by viewModel.todoItems.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddTodoDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, description ->
                viewModel.addTodo(title, description)
                showDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modern Tasks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            AddTodoFab(onClick = {
                showDialog = true
            })
        }
    ) { innerPadding ->
        TodoList(
            items = todoItems,
            onItemClick = { item ->
                viewModel.toggleCompletion(item)
            },
            onItemCheckedChange = { item, _ ->
                viewModel.toggleCompletion(item)
            },
            onDeleteClick = { item ->
                viewModel.deleteTodo(item)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
