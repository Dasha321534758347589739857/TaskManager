package org.example.Domain

enum class Priority { LOW, MEDIUM, HIGH }

enum class Status { START, PROCESS, END }


class Task ( //класс задачи
    val id: Int,
    var title: String,
    var description: String,
    var priority: Priority,
    var status: Status,)
{
    fun taskEnd() { // функция на завершение задачи
        status = Status.END
    }

    fun rename(newTitle: String) { // функция на название задачи
        require(newTitle.isNotBlank()) { "Заголовок не может быть пустым" } //валидация строки на пустое значение
        title = newTitle
    }

    companion object { // создание статичного единичного объекта, привязанного к классу
        const val MAX_TITLE_LENGTH = 100 //Это точно надо?

        fun create(
            id: Int,
            title: String,
            description: String = "",
            priority: Priority = Priority.MEDIUM,
        ): Task {
            require(title.isNotBlank()) { "Заголовок не может быть пустым" }
            require(title.length <= MAX_TITLE_LENGTH) {
                "Заголовок длиннее $MAX_TITLE_LENGTH символов"
            }
            return Task(id, title.trim(), description, priority, Status.START)
        }
    }
}