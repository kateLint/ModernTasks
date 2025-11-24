package com.keren.moderntasks.data

import com.keren.moderntasks.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTodoRepository : TodoRepository {
    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())

    override val allTodos: Flow<List<TodoItem>> = _todos

    override suspend fun insert(todo: TodoItem) {
        val currentList = _todos.value.toMutableList()
        currentList.add(todo)
        _todos.value = currentList
    }

    override suspend fun update(todo: TodoItem) {
        val currentList = _todos.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == todo.id }
        if (index != -1) {
            currentList[index] = todo
            _todos.value = currentList
        }
    }

    override suspend fun delete(todo: TodoItem) {
        val currentList = _todos.value.toMutableList()
        currentList.removeIf { it.id == todo.id }
        _todos.value = currentList
    }
}
