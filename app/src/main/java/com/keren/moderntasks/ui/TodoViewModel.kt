package com.keren.moderntasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keren.moderntasks.data.TodoRepository
import com.keren.moderntasks.model.TodoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class SortOrder {
    BY_DATE, ALPHABETICAL, BY_COMPLETION, MANUAL
}

@HiltViewModel
class TodoViewModel @Inject constructor(private val repository: TodoRepository) : ViewModel() {
    private val _sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    val todoItems: StateFlow<List<TodoItem>> = combine(
        repository.allTodos,
        _sortOrder
    ) { items, order ->
        when (order) {
            SortOrder.BY_DATE -> items.sortedByDescending { it.timestamp }
            SortOrder.ALPHABETICAL -> items.sortedBy { it.title.lowercase() }
            SortOrder.BY_COMPLETION -> items.sortedBy { it.isCompleted }
            SortOrder.MANUAL -> items.sortedBy { it.order }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

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

    fun updateTodo(item: TodoItem) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun deleteTodo(item: TodoItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun reorderTasks(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            val currentList = todoItems.value.toMutableList()
            val movedItem = currentList.removeAt(fromIndex)
            currentList.add(toIndex, movedItem)
            
            // Update order for all items
            currentList.forEachIndexed { index, item ->
                repository.update(item.copy(order = index))
            }
            
            // Switch to manual sort if not already
            if (_sortOrder.value != SortOrder.MANUAL) {
                _sortOrder.value = SortOrder.MANUAL
            }
        }
    }
}
