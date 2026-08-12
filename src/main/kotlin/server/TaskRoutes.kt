package org.example.server

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.receive
import org.example.application.CreateTaskCommand
import org.example.application.TaskService
import org.example.domain.exceptions.TaskNotFoundException
import org.example.server.TaskResponse
import org.example.server.ErrorResponse
import kotlinx.serialization.SerializationException
import org.example.domain.Task


fun Route.taskRoutes(taskService: TaskService) {

    // получить все задачи
    get("/api/tasks") {
        try {
            val tasks = taskService.getAll()
            val response = tasks.map { TaskResponse.fromDomain(it) }
            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            throw e
        }
    }

    // GET /api/tasks/{id} - получить задачу по ID
    get("/api/tasks/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw NumberFormatException("Неверный формат ID")

        val task = taskService.getById(id)
            ?: throw TaskNotFoundException(id)

        call.respond(HttpStatusCode.OK, TaskResponse.fromDomain(task))
    }

    post("/api/tasks") {
        val request = try {
            call.receive<CreateTaskRequest>()

        } catch (e: SerializationException) {
            throw e
        }

        if (request.title.isBlank()) {
            throw IllegalArgumentException("Название задачи не может быть пустой")
        }


        val task = taskService.create(CreateTaskCommand(request.title))
        call.respond(HttpStatusCode.Created, TaskResponse.fromDomain(task))
    }

    put("/api/tasks/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw NumberFormatException("Неправильный формат ID")

        val request = try {
            call.receive<UpdateTaskRequest>()
        } catch (e: SerializationException) {
            throw e
        }


        val existingTask = taskService.getById(id)
            ?: throw TaskNotFoundException(id)


        if (request.title == null ) {
            throw IllegalArgumentException("Поля должны быть изменены")
        }

        // Обновляем задачу
        val updatedTask = existingTask.copy(
            id = request.id ?: existingTask.id,
            _title = request.title ?: existingTask.title,
            _description = request.description ?: existingTask.description,
            _priority = request.priority ?: existingTask.priority,
            _status = request.status ?: existingTask.status

        )

        taskService.update(updatedTask.id,updatedTask.title, updatedTask.description, updatedTask.priority)
        call.respond(HttpStatusCode.OK, TaskResponse.fromDomain(updatedTask))
    }


    patch("/api/tasks/{id}/complete") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw NumberFormatException("Инвалидный ID")

        val success = taskService.markDone(id)
        if (success == null) {
            throw TaskNotFoundException(id)
        }

        val updatedTask = taskService.getById(id)
        call.respond(HttpStatusCode.OK, TaskResponse.fromDomain(updatedTask!!))
    }

    delete("/api/tasks/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw NumberFormatException("Неверный формат ID")

        val existingTask = taskService.getById(id)
            ?: throw TaskNotFoundException(id)

        val deleted = taskService.delete(id)
        if (deleted == null) {
            throw IllegalStateException("Задача с номером $id не найдена")
        }

        call.respond(
            HttpStatusCode.OK,
            DeleteResponse(
                status = "success",
                id = id,
                message = "Задача с ID $id успешно удалена"
            )
        )
    }


}