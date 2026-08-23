package org.example.infrastructure

import org.example.application.TaskRepository
import org.example.domain.Priority
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

    override fun getSortedByPriority(): List<Task> {
        return tasks.values.sortedBy {
            when (it.priority) {
                Priority.HIGH -> 1
                Priority.MEDIUM -> 2
                Priority.LOW -> 3
            }
        }
    }

    override fun getTasks(
        status: Status?,
        sortBy: String?,
        limit: Int,
        offset: Int
    ): List<Task> {
        val actualLimit = minOf(limit, 100)

        var result = tasks.values.toList()

        // Фильтрация по статусу
        if (status != null) {
            result = result.filter { it.status == status }
        }


        result = when (sortBy?.lowercase()) {
            "priority" -> result.sortedBy {
                when (it.priority) {
                Priority.HIGH -> 1
                Priority.MEDIUM -> 2
                Priority.LOW -> 3
            } }
            "priority_desc" -> result.sortedByDescending {
                when (it.priority) {
                Priority.HIGH -> 1
                Priority.MEDIUM -> 2
                Priority.LOW -> 3
            } }
            "created_at" -> result.sortedBy { it.createdAt }
            "created_at_desc" -> result.sortedByDescending { it.createdAt }
            "title" -> result.sortedBy { it.title }
            "title_desc" -> result.sortedByDescending { it.title }
            else -> result.sortedBy { it.id }
        }


        return result.drop(offset).take(actualLimit)
    }

    override fun getTotalCount(status: Status?): Int {
        var result = tasks.values.toList()
        if (status != null) {
            result = result.filter { it.status == status }
        }
        return result.size
    }

}
