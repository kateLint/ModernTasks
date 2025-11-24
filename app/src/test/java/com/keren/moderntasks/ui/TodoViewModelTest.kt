package com.keren.moderntasks.ui

import com.keren.moderntasks.MainDispatcherRule
import com.keren.moderntasks.data.FakeTodoRepository
import com.keren.moderntasks.model.TodoItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeTodoRepository
    private lateinit var viewModel: TodoViewModel

    @Before
    fun setup() {
        repository = FakeTodoRepository()
        viewModel = TodoViewModel(repository)
    }

    @Test
    fun `addTodo adds item to repository`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.todoItems.collect()
        }
        viewModel.addTodo("Test Task", "Description")

        val items = viewModel.todoItems.value
        assertEquals(1, items.size)
        assertEquals("Test Task", items[0].title)
    }

    @Test
    fun `deleteTodo removes item from repository`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.todoItems.collect()
        }
        viewModel.addTodo("Task to delete", "")
        val item = viewModel.todoItems.value[0]

        viewModel.deleteTodo(item)

        val items = viewModel.todoItems.value
        assertTrue(items.isEmpty())
    }

    @Test
    fun `toggleCompletion updates item status`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.todoItems.collect()
        }
        viewModel.addTodo("Task", "")
        val item = viewModel.todoItems.value[0]

        viewModel.toggleCompletion(item)

        val updatedItem = viewModel.todoItems.value[0]
        assertTrue(updatedItem.isCompleted)
    }

    @Test
    fun `setSortOrder updates sort order`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.todoItems.collect()
        }
        viewModel.setSortOrder(SortOrder.ALPHABETICAL)
        assertEquals(SortOrder.ALPHABETICAL, viewModel.sortOrder.value)
    }

    @Test
    fun `sorting works correctly`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.todoItems.collect()
        }
        viewModel.addTodo("B Task", "")
        viewModel.addTodo("A Task", "")

        viewModel.setSortOrder(SortOrder.ALPHABETICAL)

        val items = viewModel.todoItems.value
        assertEquals("A Task", items[0].title)
        assertEquals("B Task", items[1].title)
    }

    @Test
    fun `updateOrder updates item order and switches to MANUAL sort`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.todoItems.collect()
        }
        viewModel.addTodo("Task 1", "")
        viewModel.addTodo("Task 2", "")
        
        // Initial state: Task 2 (newer) is first by default sort (BY_DATE)
        val initialItems = viewModel.todoItems.value
        assertEquals("Task 2", initialItems[0].title)
        assertEquals("Task 1", initialItems[1].title)

        // Swap them
        val reorderedList = listOf(initialItems[1], initialItems[0])
        viewModel.updateOrder(reorderedList)
        
        advanceUntilIdle()

        // Verify order updated
        val finalItems = viewModel.todoItems.value
        assertEquals("Task 1", finalItems[0].title)
        assertEquals("Task 2", finalItems[1].title)
        
        // Verify sort order switched to MANUAL
        assertEquals(SortOrder.MANUAL, viewModel.sortOrder.value)
    }
}
