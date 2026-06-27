package org.example.Application

import org.example.Domain.Status
import org.example.Domain.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TaskServiceTest {

    private val repository = mockk<TaskRepository>(relaxed = true)
    private val service = TaskService(repository)

    @Test
    fun `create adds task to repository`() {
        // Arrange
        every { repository.nextId() } returns 1

        // Act
        val task = service.create(CreateTaskCommand(title = "Написать тест"))

        // Assert
        assertEquals("Написать тест", task.title)
        verify { repository.add(task) }
    }

    @Test
    fun `getById throws when task not found`() {
        // Arrange
        every { repository.getById(42) } returns null

        // Act + Assert
        assertFailsWith<TaskNotFoundException> {
            service.getById(42)
        }
    }

    @Test
    fun `markDone updates status and saves`() {
        // Arrange
        val task = Task.create(id = 1, title = "Задача")
        every { repository.getById(1) } returns task

        // Act
        val result = service.markDone(1)

        // Assert
        assertEquals(Status.END, result.status)
        verify { repository.update(task) }
    }
}