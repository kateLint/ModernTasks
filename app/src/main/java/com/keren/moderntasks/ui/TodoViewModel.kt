package com.keren.moderntasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keren.moderntasks.data.TodoRepository
import com.keren.moderntasks.model.TodoItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(private val repository: TodoRepository) : ViewModel() {
    val todoItems: StateFlow<List<TodoItem>> = repository.allTodos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTodo(title: String, description: String) {
        viewModelScope.launch {
            repository.insert(TodoItem(title = title, description = description))
        }
    }

    fun toggleCompletion(item: TodoItem) {
        viewModelScope.launch {
            repository.update(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun deleteTodo(item: TodoItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }
}
