package org.example.application

import org.example.domain.Priority
import org.example.domain.Status
import org.example.domain.Task

class TaskService(private val repository: TaskRepository) {

    fun create(command: CreateTaskCommand): Task {
        val task = Task.create(
            id = repository.nextId(),
            title = command.title,
            description = command.description,
            priority = command.priority,
        )

        repository.add(task)
        return task
    }

    fun getById(id: Int): Task =
        repository.getById(id) ?: throw EntityNotFoundException(id)

    fun getAll(): List<Task> = repository.getAll()

    fun getSortedByPriority(): List<Task> = repository.getSortedByPriority()

    fun getByStatus(status:Status): List<Task> = repository.getByStatus(status)

    fun markDone(id: Int): Task {
        val task = getById(id)
        task.complete()
        repository.update(task)
        return task
    }

    fun rename(id: Int, newTitle: String): Task {
        val task = getById(id)
        task.rename(newTitle)
        repository.update(task)
        return task
    }

    fun delete(id: Int) {
        getById(id)
        repository.delete(id)
    }

    fun update(id: Int, newTitle: String, newDescription: String, newPriority: Priority): Task {
        val task = getById(id)
        task.update(newTitle, newDescription, newPriority)
        repository.update(task)
        return task
    }



}