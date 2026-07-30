package org.example.server

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.example.application.TaskService
import org.example.server.TaskResponse
import org.example.server.ErrorResponse


fun Route.taskRoutes(taskService: TaskService) {

    // получить все задачи
    get("/api/tasks") {
        try {
            val tasks = taskService.getAll()
            val response = tasks.map { TaskResponse.fromDomain(it) }
            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = "Ошибка сервера",
                    message = e.message ?: "Неизвестная ошибка"
                )
            )
        }
    }

    // GET /api/tasks/{id} - получить задачу по ID
    get("/api/tasks/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        error = "Прохой запрос",
                        message = "Неверный формат ID"
                    )
                )
                return@get
            }

            val task = taskService.getById(id)

            if (task == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        error = "Не найдено",
                        message = "Забача с идентификатором $id не нашлась"
                    )
                )
                return@get
            }

            call.respond(HttpStatusCode.OK, TaskResponse.fromDomain(task))

        } catch (e: NumberFormatException) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    error = "Плохой запрос",
                    message = "ID - номер"
                )
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = "Ошибка сервера",
                    message = e.message ?: "Неизвестная ошибка"
                )
            )
        }
    }
}