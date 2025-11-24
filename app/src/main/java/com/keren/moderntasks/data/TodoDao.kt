package com.keren.moderntasks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.keren.moderntasks.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY timestamp DESC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(item: TodoItem)

    @Update
    suspend fun updateTodo(item: TodoItem)

    @Delete
    suspend fun deleteTodo(item: TodoItem)
}
