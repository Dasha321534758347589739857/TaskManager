package org.example.infrastructure

import org.example.domain.Priority
import org.example.domain.Task
import kotlin.test.*
import java.io.File

class FileTaskRepositoryTest {

    private lateinit var testFile: File
    private lateinit var repository: FileTaskRepository

    @BeforeTest
    fun setup() {
        // Создаем временный файл для каждого теста
        testFile = File.createTempFile("tasks_test", ".json")
        testFile.deleteOnExit()
        repository = FileTaskRepository(testFile)
    }

    @AfterTest
    fun cleanup() {
        // Удаляем файл после каждого теста
        if (testFile.exists()) {
            testFile.delete()
        }
    }



    @Test
    fun `add saves task to file and loadAll loads it correctly`() {
        // Arrange
        val task = Task.create(
            id = 1,
            title = "Тестовая задача",
            description = "Описание задачи",
            priority = Priority.HIGH
        )

        // Act
        repository.add(task)
        val loadedTasks = repository.getAll()

        // Assert
        assertEquals(1, loadedTasks.size)
        val loadedTask = loadedTasks.first()
        assertEquals(task.id, loadedTask.id)
        assertEquals(task.title, loadedTask.title)
        assertEquals(task.description, loadedTask.description)
        assertEquals(task.priority, loadedTask.priority)
        assertEquals(task.status, loadedTask.status)
        assertTrue(testFile.exists())
        assertTrue(testFile.length() > 0)
    }



    @Test
    fun `loadAll returns empty list when file does not exist`() {
        // Arrange
        testFile.delete() // Удаляем файл

        // Act
        val tasks = repository.getAll()

        // Assert
        assertTrue(tasks.isEmpty())
    }


    @Test
    fun `nextId returns 1 when file is empty`() {
        // Act
        val nextId = repository.nextId()

        // Assert
        assertEquals(1, nextId)
    }


    @Test
    fun `getById returns task when exists`() {
        // Arrange
        val task = Task.create(id = 1, title = "Тестовая задача")
        repository.add(task)

        // Act
        val foundTask = repository.getById(1)

        // Assert
        assertNotNull(foundTask)
        assertEquals(task.id, foundTask.id)
        assertEquals(task.title, foundTask.title)
    }


    @Test
    fun `update modifies existing task and saves to file`() {

        val task = Task.create(id = 1, title = "Старый заголовок")
        repository.add(task)


        task.rename("Новый заголовок")
        repository.update(task)


        val updatedTask = repository.getById(1)
        assertNotNull(updatedTask)
        assertEquals("Новый заголовок", updatedTask.title)


        val allTasks = repository.getAll()
        assertEquals(1, allTasks.size)
        assertEquals("Новый заголовок", allTasks.first().title)
    }



    @Test
    fun `delete removes task from file`() {
        // Arrange
        val task = Task.create(id = 1, title = "Задача для удаления")
        repository.add(task)

        // Act
        repository.delete(1)

        // Assert
        assertNull(repository.getById(1))
        assertTrue(repository.getAll().isEmpty())
    }








}