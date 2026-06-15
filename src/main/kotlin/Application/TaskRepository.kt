package org.example.Application

import org.example.Domain.Task

interface TaskRepository {
    fun add(task: Task) //добавление задачи
    fun getById(id: Int): Task?     // null, если не найдено(получение по id)
    fun getAll(): List<Task> // получение всех задач
    fun update(task: Task) // изменение задачи
    fun delete(id: Int) // удаление задачи
    fun nextId(): Int // выдача следующего свободного id
}