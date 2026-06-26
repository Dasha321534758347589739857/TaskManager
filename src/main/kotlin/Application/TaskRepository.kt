package org.example.Application

import org.example.Domain.Task

/**
 * Интерфейс [TaskRepository] описывает взаимодействие с данными задачи.
 *
 * Предоставляет базовые CRUD-операции (Create, Read, Update, Delete) для управления задачами в системе.
 *
 * Все методы, изменяющие состояние хранилища (add, update, delete), должны гарантировать атомарность операций.
 * В случае ошибки реализации должны выбрасывать соответствующие исключения.
 *
 * @see Task Класс описания объекта задачи.
 */
interface TaskRepository {

    /**
     * Функция [add] добавляет новую задачу в хранилище.
     *
     * Задаче автоматически присваивается уникальный идентификатор через метод [nextId].
     *
     * @param task Объект задачи для добавления.
     * @see nextId
     * @see Infrastructure.InMemoryTaskRepository
     */

    fun add(task: Task)

    /**
     * Функция [getById] находит задачу в хранилище по id.
     *
     * Возращающийся объект не должен быть равен 'null'. В функции проверка объекта [Task] на нулевое значение.
     *
     * @param id Объект задачи для добавления.
     * @return [Task]
     * @see Infrastructure.InMemoryTaskRepository
      */

    fun getById(id: Int): Task?

    /**
     * Функция [getAll] выводит все имеющиеся задачи в репозитории.
     *
     * @return [List<Task>] Список всех задач типа [Task]
     * @see Infrastructure.InMemoryTaskRepository
     */

    fun getAll(): List<Task>

    /**
     * Функция [update] обновляет конкретную задачу.
     *
     * @param task Объект класса [Task]
     * @see Infrastructure.InMemoryTaskRepository
     */

    fun update(task: Task)

    /**
     * Функция [delete] удаляет задачу по id.
     *
     * Функция должна проверять на ненулевое значение выбранного из репозитория объекта.
     * Проверка на переданный id, значение id не должно быть 'null'.
     *
     * @param id
     * @see Infrastructure.InMemoryTaskRepository
     */

    fun delete(id: Int)

    /**
     * Функция [nextId] является генератором id для каждой новой задачи.
     *
     * Используется внутри функции [add] и [TaskService.create].
     *
     * @see Infrastructure.InMemoryTaskRepository
     */

    fun nextId(): Int
}