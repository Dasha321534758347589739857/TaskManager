package org.example.infrastructure

import org.example.domain.Priority
import org.example.domain.Status
import kotlinx.serialization.Serializable
import org.example.domain.Task

@Serializable
data class TaskDto(
    val id: Int,
    val title: String,
    val description: String,
    val priority: Priority,
    val status: Status,
){
    fun toTask(): Task = Task(
        id = id,
        _title = title,
        _description = description,
        _priority = priority,
        _status = status
    )

    companion object {

        fun fromTask(task: Task): TaskDto = TaskDto(
            id = task.id,
            title = task.title,
            description = task.description,
            priority = task.priority,
            status = task.status
        )
    }
}
