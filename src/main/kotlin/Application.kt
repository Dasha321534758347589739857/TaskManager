package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.application.TaskService
import org.example.infrastructure.FileTaskRepository
import org.example.server.taskRoutes

fun main() {

    val repository = FileTaskRepository(java.io.File("tasks.json"))
    val taskService = TaskService(repository)

    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        module(taskService)
    }.start(wait = true)
}


@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: Long,
    val service: String
)

fun Application.module(taskService: TaskService) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    routing {
        get("/health") {
            val response = HealthResponse(
                status = "OK",
                timestamp = System.currentTimeMillis(),
                service = "Task Manager API"
            )
            call.respond(response)
        }
        taskRoutes(taskService)

    }
}