package org.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.application.TaskService
import org.example.infrastructure.FileTaskRepository
import org.example.web.taskRoutes
import org.example.web.dto.ErrorResponse
import org.example.domain.exceptions.TaskNotFoundException
import org.example.domain.exceptions.InvalidEnumException
import java.net.ServerSocket
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.uri
import kotlinx.serialization.SerializationException
import org.example.infrastructure.PostgresTaskRepository


fun main() {

    System.setOut(java.io.PrintStream(System.out, true, "UTF-8"))
    System.setErr(java.io.PrintStream(System.err, true, "UTF-8"))

    try {
        Class.forName("org.postgresql.Driver")
        println(" PostgreSQL Driver загружен успешно!")
    } catch (e: ClassNotFoundException) {
        println(" PostgreSQL Driver НЕ НАЙДЕН!")
        println(" Проверьте зависимость в build.gradle.kts:")
        println("   implementation(\"org.postgresql:postgresql:42.7.5\")")
        e.printStackTrace()
        return
    }

    val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/taskmanager"
    val dbUser = System.getenv("DB_USER") ?: "taskuser"
    val dbPassword = System.getenv("DB_PASSWORD") ?: "taskpass"
    println("Подключение к PostgreSQL: $dbUrl")

    //  ПРОВЕРКА ПОДКЛЮЧЕНИЯ К БД
    try {
        val connection = java.sql.DriverManager.getConnection(dbUrl, dbUser, dbPassword)
        println(" Подключение к PostgreSQL успешно!")
        connection.close()
    } catch (e: Exception) {
        println(" Ошибка подключения к PostgreSQL: ${e.message}")
        e.printStackTrace()
        return
    }

    val repository = PostgresTaskRepository(dbUrl, dbUser, dbPassword)
    val taskService = TaskService(repository)

    // ПРОВЕРКА ЧТЕНИЯ ДАННЫХ
    try {
        val tasks = repository.getAll()
        println(" Загружено задач из БД: ${tasks.size}")
        tasks.forEach { println("  - ${it.id}: ${it.title} (${it.status})") }
    } catch (e: Exception) {
        println(" Ошибка чтения данных: ${e.message}")
        e.printStackTrace()
    }

    val port = findAvailablePort(8081)
    println("Сервер находится на порте: $port")

    val server = embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module(taskService)
    }
    Runtime.getRuntime().addShutdownHook(Thread {
        println("Сервер не отвечает")
        server.stop(1000, 2000)
        println("Остановка сервера")
    })

    server.start(wait = true)
}

fun findAvailablePort(startPort: Int): Int {
    var port = startPort
    while (port < startPort + 100) {
        try {
            ServerSocket(port).close()
            println(" Найден свободный порт: $port")
            return port
        } catch (e: java.net.BindException) {
            println("Порт $port занят, попробуйте другой")
            port++
        }
        catch (e: Exception) {
            println("Ошибка при проверке порта $port: ${e.message}")
            port++
        }
    }
    error("Свободный порт не найден в диапазоне $startPort-${startPort + 100}")
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

    install(StatusPages) {
        exception<TaskNotFoundException> { call, cause ->
            val path = call.request.uri
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse.notFound(
                    message = cause.message ?: "Задача не найдена",
                    path = path
                )
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            val path = call.request.uri
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse.badRequest(
                    message = cause.message ?: "Невалидно",
                    path = path
                )
            )
        }

        exception<InvalidEnumException> { call, cause ->
            val path = call.request.uri
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse.badRequest(
                    message = cause.message ?: "Invalid enum value",
                    path = path,
                    details = mapOf(
                        "enumName" to cause.enumName,
                        "invalidValue" to cause.value
                    )
                )
            )
        }



        exception<BadRequestException> { call, cause ->
            val path = call.request.uri
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse.badRequest(
                    message = cause.message ?: "Bad request",
                    path = path
                )
            )
        }
        exception<SerializationException> { call, cause ->
            val path = call.request.uri
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse.badRequest(
                    message = "Invalid JSON format: ${cause.message}",
                    path = path,
                    details = mapOf(
                        "expectedFormat" to "Valid JSON with correct fields"
                    )
                )
            )
        }

        exception<NumberFormatException> { call, cause ->
            val path = call.request.uri
            val id = call.parameters["id"]
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse.badRequest(
                    message = "Invalid ID format: '$id'. ID must be a valid number",
                    path = path,
                    details = mapOf(
                        "invalidValue" to (id ?: "null"),
                        "expectedType" to "Integer"
                    )
                )
            )
        }

        exception<Throwable> { call, cause ->
            val path = call.request.uri
            println("Unhandled exception: ${cause.message}")
            cause.printStackTrace()
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse.internalServerError(
                    message = "An unexpected error occurred: ${cause.message}",
                    path = path
                )
            )
        }


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