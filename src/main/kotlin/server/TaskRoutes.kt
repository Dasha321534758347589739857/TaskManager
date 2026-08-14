package org.example.server

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.receive
import org.example.application.CreateTaskCommand
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

    post("/api/tasks") {
        try {
            val request = call.receive<CreateTaskRequest>()

            if (request.title.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        error = "Неверный запрос",
                        message = "Название не может быть пустым"
                    )
                )
                return@post
            }

            val task = taskService.create(CreateTaskCommand(request.title))
            call.respond(
                HttpStatusCode.Created,
                TaskResponse.fromDomain(task)
            )

        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = "Ошибка сервера",
                    message = e.message ?: "Ошибка создания"
                )
            )
        }
    }

    put("/api/tasks/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        error = "Плохой запрос",
                        message = "Неправильный формат ID"
                    )
                )
                return@put
            }

            val request = call.receive<UpdateTaskRequest>()


            val existingTask = taskService.getById(id)
            if (existingTask == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        error = "Не найдено",
                        message = "Задача с ID $id не нашлась"
                    )
                )
                return@put
            }


            if (request.title == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        error = "Плохой запрос",
                        message = "Поля должны быть изменены"
                    )
                )
                return@put
            }


            val updatedTask = existingTask.copy(
                id = request.id ?: existingTask.id,
                _title = request.title ?: existingTask.title,
                _description = request.description ?: existingTask.description,
                _priority = request.priority ?: existingTask.priority,
                _status = request.status ?: existingTask.status

            )

            taskService.update(updatedTask.id,updatedTask.title, updatedTask.description, updatedTask.priority)
            call.respond(
                HttpStatusCode.OK,
                TaskResponse.fromDomain(updatedTask)
            )

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
                    message = e.message ?: "Ошибка изменения"
                )
            )
        }
    }

    // PATCH /api/tasks/{id}/complete - отметить задачу как выполненную
    patch("/api/tasks/{id}/complete") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        error = "Плохой запрос",
                        message = "Инвалидный ID"
                    )
                )
                return@patch
            }

            val success = taskService.markDone(id)


            if (success == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        error = "Not Found",
                        message = "Задача с ID $id не найдена"
                    )
                )
                return@patch
            }

            // Получаем обновленную задачу
            val updatedTask = taskService.getById(id)
            call.respond(
                HttpStatusCode.OK,
                TaskResponse.fromDomain(updatedTask!!)
            )

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
                    message = e.message ?: "Ошибка удаления"
                )
            )
        }
    }

    // DELETE /api/tasks/{id} - удалить задачу
    delete("/api/tasks/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        error = "Плохой запрос",
                        message = "Неверный формат ID"
                    )
                )
                return@delete
            }

            // Проверяем, существует ли задача
            val existingTask = taskService.getById(id)
            if (existingTask == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        error = "Not Found",
                        message = "Задача с номером $id не найдена"
                    )
                )
                return@delete
            }

            val deleted = taskService.delete(id)

            if (deleted !== null) {
                call.respond(
                    HttpStatusCode.OK,
                    DeleteResponse(
                        status = "успешно",
                        id = id,
                        message = "Ошибка с $id удачного удаления"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(
                        error = "Internal Server Error",
                        message = "Ошибка удаления задачи по номеру $id"
                    )
                )
            }

        } catch (e: NumberFormatException) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    error = "Плохой запрос",
                    message = "ID должен быть номером"
                )
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = "Ошибка сервера",
                    message = e.message ?: "Ошибка удаления задачи"
                )
            )
        }
    }
}