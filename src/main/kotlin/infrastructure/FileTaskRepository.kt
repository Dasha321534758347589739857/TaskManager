package org.example.infrastructure

import org.example.application.TaskRepository
import org.example.domain.Task
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.example.application.TaskService
import org.example.application.EntityNotFoundException
import org.example.domain.Priority
import org.example.domain.Status
import java.io.File

class FileTaskRepository(private val file: File) : TaskRepository {

    private val json = Json { prettyPrint = true }
    private var tasks = mutableMapOf<Int, Task>()
    private var currentId = 1

    init {
        loadAll()
    }

    private fun loadAll(): MutableList<TaskDto> {
        if (!file.exists()) return mutableListOf()
        val text = file.readText()
        if (text.isBlank()) return mutableListOf()
        return json.decodeFromString<List<TaskDto>>(text).toMutableList()

    }

    private fun saveAll(items: List<TaskDto>) {
        file.writeText(json.encodeToString(items))
    }


    override fun add(task: Task)
    {
        val tasks = loadAll()
        val newDto = TaskDto.fromTask(task)
        tasks.add(newDto)
        saveAll(tasks)

    }



    override fun getById(id: Int): Task?
    {
        val tasks = loadAll()
        val findTask = tasks.find { it.id == id }
        return findTask?.toTask()
    }


    override fun getAll(): List<Task> {
        val tasks = loadAll()
        return tasks.map {
            it.toTask()
        }
    }



    override fun update(task: Task) {

        val tasks = loadAll().toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }

        if (index != -1) {
            tasks[index] = TaskDto.fromTask(task)
            saveAll(tasks)
        } else {
            throw EntityNotFoundException(index)
        }
    }



    override fun delete(id: Int) {
        val tasks = loadAll().toMutableList()
        val removed = tasks.removeIf { it.id == id }

        if (removed) {
            saveAll(tasks)
        } else {
            throw EntityNotFoundException(id)
        }
    }



    override fun nextId(): Int {
        val tasks = loadAll()
        return if (tasks.isEmpty()) 1 else tasks.maxOf { it.id } + 1
    }


    override fun getByStatus(status: Status): List<Task> {
        val tasks = loadAll()
        val filterTasks = tasks.filter { it.status == status }
        return filterTasks.map { it.toTask() }

    }

    override fun getSortedByPriority(): List<Task> {
        val tasks = loadAll()
        val filterTasks = tasks.sortedByDescending  { it.priority}
        return filterTasks.map { it.toTask() }
    }

    override fun getTasks(
        status: Status?,
        sortBy: String?,
        limit: Int,
        offset: Int
    ): List<Task> {
        val actualLimit = minOf(limit, 100)

        var result = tasks.values.toList()


        if (status != null) {
            result = result.filter { it.status == status }
        }


        result = when (sortBy?.lowercase()) {
            "priority" -> result.sortedBy { it.priority }
            "priority_desc" -> result.sortedByDescending { it.priority }
            "created_at" -> result.sortedBy { it.createdAt }
            "created_at_desc" -> result.sortedByDescending { it.createdAt }
            "title" -> result.sortedBy { it.title }
            "title_desc" -> result.sortedByDescending { it.title }
            else -> result.sortedBy { it.id }
        }


        return result.drop(offset).take(actualLimit)
    }

    override fun getTotalCount(status: Status?): Int {
        var result = tasks.values.toList()
        if (status != null) {
            result = result.filter { it.status == status }
        }
        return result.size
    }

}