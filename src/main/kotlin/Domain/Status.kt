package org.example.Domain

/**
 * Статус задачи [Status].
 *
 * Определяет этап выполнения задачи.
 *
 * @see Task
 * @see org.example.Application.TaskService
 */
public enum class Status {

    /**
     * Начальный этап.
     *
     * Задача недавно создана. Реализация задачи ещё не началась.
     *
     * @see Task
     */

    START,

    /**
     * Этап реализации.
     *
     * Задача в процессе выполнения.
     */
    PROCESS,

    /**
     * Завершение задачи.
     *
     * Задача выполнена.
     *
     * @see Task
     */
    END
}