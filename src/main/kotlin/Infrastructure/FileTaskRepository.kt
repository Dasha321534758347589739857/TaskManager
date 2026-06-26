package org.example.Infrastructure

import org.example.Application.TaskRepository
import org.example.Domain.Task
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.example.Application.TaskService
import org.example.Application.TaskNotFoundException
import java.io.File

class FileTaskRepository(private val file: File) : TaskRepository {

    private val json = Json { prettyPrint = true }

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
            throw TaskNotFoundException(index)
        }
    }



    override fun delete(id: Int) {
        val tasks = loadAll().toMutableList()
        val removed = tasks.removeIf { it.id == id }

        if (removed) {  // Если удалили хотя бы одну задачу
            saveAll(tasks)
        } else {
            throw TaskNotFoundException(id)
        }
    }



    override fun nextId(): Int {
        val tasks = loadAll()
        return if (tasks.isEmpty()) 1 else tasks.maxOf { it.id } + 1
    }

}