package org.example.Infrastructure

import org.example.Domain.Priority
import org.example.Domain.Status
import org.example.Domain.Task
import kotlin.test.*

class InMemoryTaskRepositoryTest {

    private lateinit var repository: InMemoryTaskRepository

    @BeforeTest
    fun setup() {
        repository = InMemoryTaskRepository()
    }



    @Test
    fun `getByStatus returns tasks with specified status`() {
        // Arrange
        val task1 = Task.create(id = 1, title = "Задача 1")
        val task2 = Task.create(id = 2, title = "Задача 2")
        val task3 = Task.create(id = 3, title = "Задача 3")

        task2.complete()
        repository.add(task1)
        repository.add(task2)
        repository.add(task3)

        // Act
        val inProgressTasks = repository.getByStatus(Status.START)
        val completedTasks = repository.getByStatus(Status.END)

        // Assert
        assertEquals(2, inProgressTasks.size)
        assertEquals(1, completedTasks.size)

        assertTrue(inProgressTasks.all { it.status == Status.START })
        assertTrue(completedTasks.all { it.status == Status.END })

        assertEquals("Задача 1", inProgressTasks[0].title)
        assertEquals("Задача 3", inProgressTasks[1].title)
        assertEquals("Задача 2", completedTasks[0].title)
    }




    @Test
    fun `getSortedByPriority returns tasks sorted by priority descending`() {
        // Arrange
        val lowTask = Task.create(id = 1, title = "Низкий", priority = Priority.LOW)
        val mediumTask = Task.create(id = 2, title = "Средний", priority = Priority.MEDIUM)
        val highTask = Task.create(id = 3, title = "Высокий", priority = Priority.HIGH)

        repository.add(lowTask)
        repository.add(mediumTask)
        repository.add(highTask)

        // Act
        val sortedTasks = repository.getSortedByPriority()

        // Assert
        assertEquals(3, sortedTasks.size)
        assertEquals(Priority.HIGH, sortedTasks[0].priority)
        assertEquals("Высокий", sortedTasks[0].title)
        assertEquals(Priority.MEDIUM, sortedTasks[1].priority)
        assertEquals("Средний", sortedTasks[1].title)
        assertEquals(Priority.LOW, sortedTasks[2].priority)
        assertEquals("Низкий", sortedTasks[2].title)
    }


}