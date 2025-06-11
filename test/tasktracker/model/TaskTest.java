package tasktracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldBeEqualIfIdsMatch() {
        Task task1 = new Task(1, "Task1", "Desc", Status.NEW);
        Task task2 = new Task(1, "Task2", "Other desc", Status.IN_PROGRESS);
        assertEquals(task1, task2, "Tasks with same id must be equal");
    }

    @Test
    void shouldUpdateTaskFieldsCorrectly() {
        Task task = new Task(1, "Old Name", "Old Desc", Status.NEW);
        task.setName("New Name");
        task.setDescription("New Desc");
        task.setStatus(Status.DONE);
        assertEquals("New Name", task.getName());
        assertEquals("New Desc", task.getDescription());
        assertEquals(Status.DONE, task.getStatus());
    }

    @Test
    void shouldReturnCorrectToString() {
        Task task = new Task(1, "name", "desc", Status.NEW);
        String expected = "Task{id=1, name='name', description='desc'}";
        assertEquals(expected, task.toString());
    }

    @Test
    void shouldReturnSameHashCodeForSameId() {
        Task task1 = new Task(1, "Task1", "Desc", Status.NEW);
        Task task2 = new Task(1, "Task2", "Other Desc", Status.IN_PROGRESS);
        assertEquals(task1.hashCode(), task2.hashCode());
    }
}