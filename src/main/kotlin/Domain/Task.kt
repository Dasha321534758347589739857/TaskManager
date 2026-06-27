package org.example.Domain

import org.example.Domain.MAX_TITLE_LENGTH
import org.example.Domain.Priority
import org.example.Domain.Status

public class Task ( //класс задачи
    val id: Int,
    private var _title: String,
    private var _description: String,
    private var _priority: Priority,
    private var _status: Status)

{
    val title: String get()= _title
    val description: String get() = _description
    val priority: Priority get() = _priority
    val status: Status get() = _status



    fun complete() {
        _status = Status.END
    }

    fun rename(newTitle: String) {
        require(newTitle.isNotBlank()) { "Заголовок не может быть пустым" }
        _title = newTitle
    }

    fun update(newTitle: String, newDescription: String, newPriority: Priority)
    {
        require(newTitle.isNotBlank()){"Заголовок не может быть пустым"}
        require(newDescription.isNotBlank()){"Описание не может быть пустым"}

        _title = newTitle
        _description = newDescription
        _priority = newPriority


    }



    companion object {


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