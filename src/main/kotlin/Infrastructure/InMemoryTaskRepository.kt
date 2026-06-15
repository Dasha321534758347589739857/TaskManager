package org.example.Infrastructure

import org.example.Application.TaskRepository
import org.example.Domain.Task

class InMemoryTaskRepository : TaskRepository {
    private val tasks = mutableMapOf<Int, Task>() //создание пустого словаря с ключом id и структурой Task
    private var lastId = 0


        // override - это обращение дочернего элемента к методу родительского
    override fun add(task: Task) { // принимаем объект Task
        tasks[task.id] = task //записываем по id объект
    }

    override fun getById(id: Int): Task? = tasks[id] // ищем задачу по id в словаре, если нет, возвращаем 0

    override fun getAll(): List<Task> = tasks.values.toList() // вывод всего списка задач

    override fun update(task: Task) { // перезапись по id
        tasks[task.id] = task
    }

    override fun delete(id: Int) {
        tasks.remove(id)
    }

    override fun nextId(): Int = ++lastId //счётчик (условно)
}