package org.example.Application

class TaskNotFoundException(id: Int) : // исключение на "потерянный" id
    RuntimeException("Задача с id = $id не найдена")