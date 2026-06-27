#!/usr/bin/env kotlin
package org.example.Domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TaskTest {

    @Test
    fun `create returns task with TODO status`() {

        val task = Task.create(id = 1, title = "Купить хлеб")


        assertEquals("Купить хлеб", task.title)
        assertEquals(Priority.MEDIUM, task.priority)
    }

    @Test
    fun `create with blank title throws`() {
        assertFailsWith<IllegalArgumentException> {
            Task.create(id = 1, title = "   ")
        }
    }

    @Test
    fun `markDone changes status to DONE`() {
        val task = Task.create(id = 1, title = "Тест")
            task.complete()
        assertEquals(Status.END, task.status)
    }

}


