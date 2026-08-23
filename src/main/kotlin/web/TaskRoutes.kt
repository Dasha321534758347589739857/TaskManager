package org.example.web

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.receive
import org.example.application.CreateTaskCommand
import org.example.application.TaskService
import org.example.domain.exceptions.TaskNotFoundException
import kotlinx.serialization.SerializationException
import org.example.domain.Priority
import org.example.domain.Task
import org.example.domain.Status
import org.example.domain.exceptions.InvalidEnumException
import org.example.web.dto.PageResponse


fun Route.taskRoutes(taskService: TaskService) {


    get("/api/tasks") {
        val statusParam = call.request.queryParameters["status"]
        val sortParam = call.request.queryParameters["sort"]
        val limitParam = call.request.queryParameters["limit"]?.toIntOrNull()
        val offsetParam = call.request.queryParameters["offset"]?.toIntOrNull()

        val status = statusParam?.let {
            try {
                Status.valueOf(it.uppercase())
            } catch (e: IllegalArgumentException) {
                throw InvalidEnumException("status", it)
            }
        }

        val limit = limitParam ?: 20
        if (limit < 1) {
            throw IllegalArgumentException("Предел должен быть больше 0")
        }
        if (limit > 100) {
            throw IllegalArgumentException("Предел не может быть больше 100")
        }

        val offset = offsetParam ?: 0
        if (offset < 0) {
            throw IllegalArgumentException("Смещение должно быть больше или равно 0")
        }

            //TODO изучить подробнее
        val tasks = taskService.getTasks(status, sortParam, limit, offset)
        val total = taskService.getTotalCount(status)


        val response = PageResponse(
            items = tasks.map { TaskResponse.fromDomain(it) },
            limit = limit,
            offset = offset,
            total = total
        )

        call.respond(HttpStatusCode.OK, response)
    }


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
            throw IllegalArgumentException("Неверный формат JSON: ${e.message}")
        }

        if (request.title.isBlank()) {
            throw IllegalArgumentException("Название задачи не может быть пустой")
        }


        val priority = when (request.priority?.uppercase()) {
            "HIGH" -> Priority.HIGH
            "MEDIUM" -> Priority.MEDIUM
            "LOW" -> Priority.LOW
            null -> Priority.MEDIUM  // По умолчанию
            else -> throw IllegalArgumentException("Некорректный приоритет: ${request.priority}. Допустимые значения: HIGH, MEDIUM, LOW")
        }

        val task = taskService.create(CreateTaskCommand(
            title = request.title,
            description = request.description ?: "",
            priority = priority
        ) )

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


        if (request.title == null && request.description == null &&
            request.priority == null && request.status == null) {
            throw IllegalArgumentException("Для обновления должно быть указано хотя бы одно поле")
        }

        // Обновляем задачу
        val updatedTask = taskService.update(
            id = id ?: existingTask.id,
            newTitle = request.title ?: existingTask.title,
            newDescription = request.description ?: existingTask.description,
            newPriority = request.priority ?: existingTask.priority,


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