package org.example.Application

import org.example.Domain.Status
import org.example.Domain.Task

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
        repository.getById(id) ?: throw TaskNotFoundException(id)

    fun getAll(): List<Task> = repository.getAll()

    fun markDone(id: Int): Task {
        val task = getById(id)
        //task.markDone()
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
        // проверяем, что задача существует — иначе осмысленная ошибка
        getById(id)
        repository.delete(id)
    }
}