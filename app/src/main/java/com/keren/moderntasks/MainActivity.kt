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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.keren.moderntasks.model.TodoItem
import com.keren.moderntasks.ui.components.AddTodoFab
import com.keren.moderntasks.ui.components.TodoList
import com.keren.moderntasks.ui.theme.ModernTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModernTasksTheme {
                // A surface container using the 'background' color from the theme
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
    // Dummy data state
    val todoItems = remember {
        mutableStateListOf(
            TodoItem(title = "Learn Jetpack Compose", description = "Master the basics of Compose UI"),
            TodoItem(title = "Build Modern Tasks App", description = "Create a beautiful ToDo app", isCompleted = true),
            TodoItem(title = "Setup GitHub Profile", description = "Polish the profile for recruiters"),
            TodoItem(title = "Buy Groceries", description = "Milk, Eggs, Bread")
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
                todoItems.add(0, TodoItem(title = "New Task ${todoItems.size + 1}", description = "Description for new task"))
            })
        }
    ) { innerPadding ->
        TodoList(
            items = todoItems,
            onItemClick = { item ->
                // Toggle completion on click for now
                val index = todoItems.indexOf(item)
                if (index != -1) {
                    todoItems[index] = item.copy(isCompleted = !item.isCompleted)
                }
            },
            onItemCheckedChange = { item, isChecked ->
                val index = todoItems.indexOf(item)
                if (index != -1) {
                    todoItems[index] = item.copy(isCompleted = isChecked)
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
