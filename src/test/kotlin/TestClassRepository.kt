import org.example.Application.TaskRepository
import org.example.Domain.Task
import org.example.Infrastructure.InMemoryTaskRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach


class TestClassRepository{

    private lateinit var repository: TaskRepository

    @BeforeEach
    fun setUp() {

        repository = InMemoryTaskRepository()
    }

    @Test
    fun `add task in repository`() {

        val task = Task.create(id = 1, title = "Купить хлеб")

        repository.add(task)

        val found = repository.getById(1)

        assertNotNull(found)
        assertEquals(task, found)
        assertEquals("Купить хлеб", found?.title)
    }

    @Test
    fun `getById task`() {

        val task = Task.create(3, "Тестовый тест")
        repository.add(task)


        val found = repository.getById(3)


        assertNotNull(found)
        assertEquals(task, found)
    }

    @Test
    fun `getAll tasks`() {

        val tasks = repository.getAll()


        assertTrue(tasks.isEmpty())
        assertEquals(0, tasks.size)
    }

    @Test
    fun `delete task`() {
        // given
        val task = Task.create(1, "Тестовый тест")
        repository.add(task)

        // when
        repository.delete(1)

        // then
        val found = repository.getById(1)
        assertNull(found)
    }

}