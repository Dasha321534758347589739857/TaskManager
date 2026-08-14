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
    val status: Status

) {
    companion object {
        fun fromDomain(task: Task): TaskResponse {
            return TaskResponse(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = task.priority,
                status = task.status
            )
        }
    }
}

@Serializable
data class CreateTaskRequest(
    val title: String
)

@Serializable
data class UpdateTaskRequest(
    val id: Int,
    val title: String? = null,
    val description: String? = null,
    val priority: Priority,
    val status: Status

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
    val timestamp: Long = System.currentTimeMillis()
)