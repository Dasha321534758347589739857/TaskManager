package org.example.Domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TaskTestRename {

    @Test
    fun `create returns task`() {

        val task = Task.create(id = 10, title = "Сходить в баню",priority = Priority.HIGH)

        // Assert
        assertEquals("Сходить в баню", task.title)
        assertEquals(Status.START, task.status)
        assertEquals(Priority.HIGH, task.priority)
    }


    @Test
    fun `rename returns task`() {
        val task = Task.create(id = 11, title = "Тест")
        task.rename("Сбегать в аптеку")
        assertEquals("Сбегать в аптеку", task.title)

    }

}