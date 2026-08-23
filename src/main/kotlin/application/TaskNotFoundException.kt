package org.example.application

/**
 * Исключение на ненайденный объект в БД.
 *
 * Ошибка, выбрасываемая при отсутствии сущности в хранилище с заданным id.
 *
 * @param id Идентификатор задачи
 */
 public class EntityNotFoundException(id: Int) :
    RuntimeException("Задача с id = $id не найдена")