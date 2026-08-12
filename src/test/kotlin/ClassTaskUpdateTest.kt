import org.example.domain.Priority
import org.example.domain.Status
import org.example.domain.Task
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskTestUpdate {

    @Test
    fun `create returns task `() {

        val task = Task.create(id = 6, title = "Сходить в магазин",priority = Priority.HIGH)

        // Assert
        assertEquals("Сходить в магазин", task.title)
        assertEquals(Status.START, task.status)
        assertEquals(Priority.HIGH, task.priority)
    }


   /* @Test
    fun `update returns task`() {
        val task = Task.create(id = 7, title = "Тест")
        task.update("Сходить в аптеку","G", Priority.LOW)
        assertEquals("Сходить в аптеку", task.title)
        assertEquals("G", task.description)
        assertEquals(Priority.LOW, task.priority)
    }*/

}