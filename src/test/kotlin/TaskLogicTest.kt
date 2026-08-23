package org.example.application

import org.example.domain.Task
import org.example.domain.Status
import org.example.domain.Priority
import org.example.infrastructure.InMemoryTaskRepository
import org.junit.jupiter.api.BeforeEach
import kotlin.test.*

class TaskLogicTest {

       private lateinit var repository: InMemoryTaskRepository
        private lateinit var service: TaskService

        @BeforeEach
        fun setUp() {
            repository = InMemoryTaskRepository()
            service = TaskService(repository)


            val task1 = service.create(CreateTaskCommand("Task 1", priority = Priority.HIGH))
            repository.update(task1.copy(_status = Status.START))

            val task2 = service.create(CreateTaskCommand("Task 2", priority = Priority.MEDIUM))
            repository.update(task2.copy(_status = Status.PROCESS))

            val task3 = service.create(CreateTaskCommand("Task 3", priority = Priority.LOW))
            repository.update(task3.copy(_status = Status.START))

            val task4 = service.create(CreateTaskCommand("Task 4", priority = Priority.HIGH))
            repository.update(task4.copy(_status = Status.END))

            val task5 = service.create(CreateTaskCommand("Task 5", priority = Priority.MEDIUM))
            repository.update(task5.copy(_status = Status.START))
        }

        @Test
        fun `should filter tasks by status`() {
            // when
            val tasks = service.getTasks(status = Status.START)

            // then
            assertEquals(3, tasks.size)
            assertTrue(tasks.all { it.status == Status.START })
        }

        @Test
        fun `should sort tasks by priority`() {
            // when
            val tasks = service.getTasks(sortBy = "priority")

            // then
            val priorities = tasks.map { it.priority }
            assertEquals(
                listOf(Priority.HIGH, Priority.HIGH, Priority.MEDIUM, Priority.MEDIUM, Priority.LOW),
                priorities
            )
        }

        @Test
        fun `should sort tasks by priority descending`() {
            // when
            val tasks = service.getTasks(sortBy = "priority_desc")

            // then
            val priorities = tasks.map { it.priority }
            assertEquals(
                listOf(Priority.LOW, Priority.MEDIUM, Priority.MEDIUM, Priority.HIGH, Priority.HIGH),
                priorities
            )
        }

        @Test
        fun `should paginate results`() {
            // when
            val tasks = service.getTasks(limit = 2, offset = 0)

            // then
            assertEquals(2, tasks.size)
        }

        @Test
        fun `should get correct total count`() {
            // when
            val total = service.getTotalCount()

            // then
            assertEquals(5, total)
        }

        @Test
        fun `should get correct total count with filter`() {
            // when
            val total = service.getTotalCount(status = Status.START)

            // then
            assertEquals(3, total)
        }

        @Test
        fun `should combine filter sort and pagination`() {
            // when
            val tasks = service.getTasks(
                status = Status.START,
                sortBy = "priority",
                limit = 2,
                offset = 0
            )

            // then
            assertEquals(2, tasks.size)
            assertTrue(tasks.all { it.status == Status.START })
            val priorities = tasks.map { it.priority }
            assertEquals(listOf(Priority.HIGH, Priority.MEDIUM), priorities)
        }

}