package org.example.infrastructure

import org.example.application.TaskRepository
import org.example.domain.Status
import org.example.domain.Task

class InMemoryTaskRepository : TaskRepository {
    private val tasks = mutableMapOf<Int, Task>()
    private var lastId = 0



    override fun add(task: Task) {
        tasks[task.id] = task
    }

    override fun getById(id: Int): Task? = tasks[id]

    override fun getAll(): List<Task> = tasks.values.toList()

    override fun update(task: Task) {
        tasks[task.id] = task
    }

    override fun delete(id: Int) {
        tasks.remove(id)
    }

    override fun nextId(): Int = ++lastId

    override fun getByStatus(status: Status): List<Task> =
        getAll().filter { it.status == status }

    override fun getSortedByPriority(): List<Task> =
        getAll().sortedByDescending { it.priority }
}
