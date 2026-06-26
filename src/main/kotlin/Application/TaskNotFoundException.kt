package org.example.Application

/**
 * Исключение на ненайденный id объекта.
 *
 * Используется в методе [TaskService.getById]. При отсутствии искомого id в репозитории вылезает ошибка.
 * При реализации исключения необходимо обернуть его в конструкцию try-catch или добавить throws в сигнатуру.
 *
 * @param id Идентификатор задачи
 * @see TaskService.getById
 */

 public class TaskNotFoundException(id: Int) :
    RuntimeException("Задача с id = $id не найдена")