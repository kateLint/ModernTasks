package com.keren.moderntasks.data

import com.keren.moderntasks.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    val allTodos: Flow<List<TodoItem>>
    suspend fun insert(todo: TodoItem)
    suspend fun update(todo: TodoItem)
    suspend fun delete(todo: TodoItem)
}

class TodoRepositoryImpl(private val todoDao: TodoDao) : TodoRepository {
    override val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodos()

    override suspend fun insert(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }

    override suspend fun update(todo: TodoItem) {
        todoDao.updateTodo(todo)
    }

    override suspend fun delete(todo: TodoItem) {
        todoDao.deleteTodo(todo)
    }
}
