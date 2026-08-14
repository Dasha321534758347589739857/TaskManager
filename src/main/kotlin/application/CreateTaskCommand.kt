package org.example.application

import org.example.domain.Priority

/**
 * Команда [CreateTaskCommand] для создания навой задачи.
 *
 * Описание данных для задачи.
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