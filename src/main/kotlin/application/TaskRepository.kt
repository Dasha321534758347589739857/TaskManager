package org.example.application

import org.example.domain.Status
import org.example.domain.Task

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
     */

    fun add(task: Task)

    /**
     * Функция [getById] находит задачу в хранилище по id.
     *
     * Возращающийся объект не должен быть равен 'null'. В функции проверка объекта [Task] на нулевое значение.
     *
     * @param id Объект задачи для добавления.
     * @return [Task]
     * @see infrastructure.InMemoryTaskRepository
      */

    fun getById(id: Int): Task?

    /**
     * Функция [getAll] выводит все имеющиеся задачи в репозитории.
     *
     * @return [List<Task>] Список всех задач типа [Task]
     * @see infrastructure.InMemoryTaskRepository
     */

    fun getAll(): List<Task>

    /**
     * Функция [update] обновляет конкретную задачу.
     *
     * @param task Объект класса [Task]
     * @see infrastructure.InMemoryTaskRepository
     */

    fun update(task: Task)

    /**
     * Функция [delete] удаляет задачу по id.
     *
     * Функция должна проверять на ненулевое значение выбранного из репозитория объекта.
     * Проверка на переданный id, значение id не должно быть 'null'.
     *
     * @param id
     * @see infrastructure.InMemoryTaskRepository
     */

    fun delete(id: Int)

    /**
     * Функция [nextId] является генератором id для каждой новой задачи.
     *
     * Используется внутри функции [add] и [TaskService.create].
     *
     * @see infrastructure.InMemoryTaskRepository
     */

    fun nextId(): Int

    /**
     * Функция [getByStatus] сортирует задачи по статусу.
     *
     * Функция должна сортировать пользователю при запросе на весь список,
     * сортировку по статусу выполнения задачи.
     *
     * @see infrastructure.InMemoryTaskRepository
     */

    fun getByStatus(status: Status) : List<Task>

    /**
     * Функция [getSortedByPriority] сортирует задачи по приоритетности.
     *
     * Функция должна сортировать пользователю при запросе на весь список,
     * сортировку по приоритету выполнения задачи.
     *
     * @see infrastructure.InMemoryTaskRepository
     */

    fun getSortedByPriority(): List<Task>




}