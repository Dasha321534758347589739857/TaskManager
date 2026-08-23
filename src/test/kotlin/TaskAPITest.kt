package org.example.web

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.example.application.CreateTaskCommand
import org.example.application.TaskService
import org.example.domain.Status
import org.example.infrastructure.InMemoryTaskRepository
import org.example.web.dto.PageResponse
import io.ktor.serialization.kotlinx.json.json

import kotlin.test.*

class TaskAPITest {

    @Test
    fun `should get tasks with pagination`() = testApplication {

        application {

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            val repository = InMemoryTaskRepository()
            val service = TaskService(repository)

            routing {
                taskRoutes(service)
            }
        }


        repeat(5) { i ->
            client.post("/api/tasks") {
                contentType(ContentType.Application.Json)
                setBody("""{"title":"Task $i"}""")
            }
        }

        val response = client.get("/api/tasks?limit=2&offset=0")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.bodyAsText()
        val page = Json.decodeFromString<PageResponse<TaskResponse>>(body)

        assertEquals(2, page.items.size)
        assertEquals(2, page.limit)
        assertEquals(0, page.offset)
        assertEquals(5, page.total)
    }

    @Test
    fun `should filter tasks by status`() = testApplication {

        val repository = InMemoryTaskRepository()
        val service = TaskService(repository)


        application {

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            routing {
                taskRoutes(service)
            }
        }

        val task1 = service.create(CreateTaskCommand("Task 1"))
        val task2 = service.create(CreateTaskCommand("Task 2"))


        val updatedTask1 = task1.copy(_status = Status.START)
        val updatedTask2 = task2.copy(_status = Status.PROCESS)
        repository.update(updatedTask1)
        repository.update(updatedTask2)


        val response = client.get("/api/tasks?status=START")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.bodyAsText()
        val page = Json.decodeFromString<PageResponse<TaskResponse>>(body)

        assertEquals(1, page.total)
        assertTrue(page.items.all { it.status == Status.START })
    }
}