package tasktracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void shouldBeEqualIfIdsMatch() {
        Subtask sub1 = new Subtask(1, "Sub1", "Desc", Status.NEW, 10);
        Subtask sub2 = new Subtask(1, "Sub2", "Other desc", Status.IN_PROGRESS, 20);
        assertEquals(sub1, sub2);
    }

    @Test
    void shouldReturnCorrectEpicId() {
        Subtask subtask = new Subtask(1, "Sub1", "Desc", Status.NEW, 42);
        assertEquals(42, subtask.getEpicId());
    }

    @Test
    void shouldReturnCorrectToString() {
        Subtask subtask = new Subtask(1, "Sub1", "Desc", Status.IN_PROGRESS, 99);
        String expected = "Subtask{id=1, name='Sub1', description='Desc', status=IN_PROGRESS, epicId=99}";
        assertEquals(expected, subtask.toString());
    }
}