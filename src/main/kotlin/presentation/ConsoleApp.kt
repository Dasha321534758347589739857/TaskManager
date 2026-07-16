package org.example.presentation

import org.example.application.CreateTaskCommand
import org.example.application.EntityNotFoundException
import org.example.application.TaskService
import org.example.domain.Priority
import org.example.domain.Status

class ConsoleApp(private val service: TaskService) {

    fun run() {
        while (true) {
            printMenu()
            when (readLine()?.trim()) {
                "1" -> showAll()
                "2" -> addTask()
                "3" -> markDone()
                "4" -> deleteTask()
                "5" -> updateTask()
                "0" -> {
                    println("Завершение работы")
                    return
                }
                else -> println("Неизвестная команда, попробуте ещё раз.")
            }
        }
    }

    private fun printMenu() {
        println(
            """
            |
            |=== Менеджер задач ===
            |1 — показать все
            |2 — добавить
            |3 — отметить выполненной
            |4 — удалить
            |5 - обновить задачу
            |0 — выход
            """.trimMargin()
        )
    }

    private fun showAll() {
        val tasks = service.getAll()

        if (tasks.isEmpty()) {
            println("Задач пока нет.")
            return
        }
        tasks.forEach { task ->
            val mark = if (task.status == Status.END) "[x]" else "[ ]"
            println("$mark #${task.id} ${task.title} (${task.priority})")
        }
    }

    private fun addTask() {
        print("Заголовок: ")
        val title = readLine()?.trim().orEmpty()
        try {
            val task = service.create(CreateTaskCommand(title = title))
            println("Создана задача #${task.id}")
        } catch (e: IllegalArgumentException) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun markDone() {
        val id = askId() ?: return
        try {
            service.markDone(id)
            println("Задача #$id отмечена выполненной.")
        } catch (e: EntityNotFoundException) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun deleteTask() {
        val id = askId() ?: return
        try {
            service.delete(id)
            println("Задача #$id удалена.")
        } catch (e: EntityNotFoundException) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun updateTask() {
        val id = askId() ?: return

        print("Введите заголовок: ")
        val newTitle = readLine()?.trim().orEmpty()
        print("Введите описание: ")
        val newDescription = readLine()?.trim().orEmpty()


            println(
                """
            | Введите приоритет
            |1 — Hight
            |2 — Medium
            |3 — Low
            |0 — выход
            """.trimMargin()
            )
            when (readLine()?.trim()) {
                "1" -> service.update(id, newTitle, newDescription, Priority.HIGH)
                "2" -> service.update(id, newTitle, newDescription, Priority.MEDIUM)
                "3" -> service.update(id, newTitle, newDescription, Priority.LOW)

                "0" -> {
                    println("Отмена")

                }
                else -> println("Неизвестная команда, попробуте ещё раз.")
            }







    }

    private fun askId(): Int? {
        print("Введите id: ")

        val id = readLine()?.trim()?.toIntOrNull()
        if (id == null) {
            println("Это не число.")
        }
        return id
    }
}