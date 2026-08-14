package org.example.application

/**
 * Исключение на ненайденный объект в БД.
 *
 * При отсутствии искомого id в БД вылезает ошибка.
 *
 * @param id Идентификатор задачи
 */
 public class EntityNotFoundException(id: Int) :
    RuntimeException("Задача с id = $id не найдена")