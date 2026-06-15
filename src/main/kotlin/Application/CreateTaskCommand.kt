package org.example.Application

import org.example.Domain.Priority

data class CreateTaskCommand( //объект с данными задачи
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
)