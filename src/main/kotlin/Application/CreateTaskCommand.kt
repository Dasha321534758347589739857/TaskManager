package org.example.Application

import org.example.Domain.Priority

/**
 * Команда [CreateTaskCommand] для создания навой задачи.
 *
 * Нужна для автоматической генерации id в программе без участия пользователя.
 * Пользователю же поступают те данные, которые ему необходимы для создания задачи.
 *
 * @property title Заголовок задачи.
 * @property description Описание задачи.
 * @property priority Приоритет задачи. По умолчанию приоритет средний.
 * @see org.example.Domain.Task
 * @see TaskService
 */

public data class CreateTaskCommand(
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
)