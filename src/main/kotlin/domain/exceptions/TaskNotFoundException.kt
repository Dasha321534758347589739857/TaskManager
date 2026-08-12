package org.example.domain.exceptions

class TaskNotFoundException(val taskId: Int) : RuntimeException("Задача с ID $taskId не найдена")