package org.example.Infrastructure

import org.example.Domain.Priority
import org.example.Domain.Status
import kotlinx.serialization.Serializable
import org.example.Domain.Task

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
