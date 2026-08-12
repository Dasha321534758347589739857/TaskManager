package org.example.server

import kotlinx.serialization.Serializable
import org.example.domain.Priority
import org.example.domain.Status
import org.example.domain.Task

//DTO для ответа на запрос (разделение слоёв) - выбираем поля, которые отправляем пользователю

@Serializable
data class TaskResponse(
    val id: Int,
    val title: String,
    val description: String,
    val priority: Priority,
    val status: Status,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String

) {
    companion object {
        fun fromDomain(task: Task): TaskResponse {
            return TaskResponse(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = task.priority,
                status = task.status,
                completed = task.isDone,
                createdAt = task.createdAt.toString(),
                updatedAt = task.updatedAt.toString()
            )
        }
    }
}

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val priority: String? = null
)

@Serializable
data class UpdateTaskRequest(
    val id: Int,
    val title: String? = null,
    val description: String? = null,
    val priority: Priority,
    val status: Status,
    val completed: Boolean = false

)

@Serializable
data class DeleteResponse(
    val status: String,
    val id: Int,
    val message: String = "Записка удалена"
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val path: String? = null,
    val details: Map<String, String>? = null
){
    companion object {
    }
    fun badRequest(
        message: String,
        path: String? = null,
        details: Map<String, String>? = null
    ) = ErrorResponse(
        error = "Плохой запрос",
        message = message,
        path = path,
        details = details
    )

    fun notFound(
        message: String,
        path: String? = null
    ) = ErrorResponse(
        error = "Не найдено",
        message = message,
        path = path
    )

    fun conflict(
        message: String,
        path: String? = null
    ) = ErrorResponse(
        error = "Конфликт",
        message = message,
        path = path
    )

    fun internalServerError(
        message: String,
        path: String? = null
    ) = ErrorResponse(
        error = "Ошибка сервера",
        message = message,
        path = path
    )}