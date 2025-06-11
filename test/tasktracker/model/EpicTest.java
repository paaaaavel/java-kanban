package tasktracker.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldBeEqualIfIdsMatch() {
        Epic epic1 = new Epic(1, "Epic1", "Desc", Status.NEW);
        Epic epic2 = new Epic(1, "Epic2", "Other desc", Status.DONE);
        assertEquals(epic1, epic2);
    }

    @Test
    void shouldAddAndRemoveSubtaskIds() {
        Epic epic = new Epic(1, "Epic1", "Desc", Status.NEW);
        assertTrue(epic.getSubtaskIds().isEmpty());
        epic.addSubtaskId(42);
        assertEquals(List.of(42), epic.getSubtaskIds());
        epic.removeSubtaskId(42);
        assertTrue(epic.getSubtaskIds().isEmpty());
    }

    @Test
    void shouldNotAllowEpicToContainItself() {
        Epic epic = new Epic(1, "Epic1", "Desc", Status.NEW);
        epic.addSubtaskId(epic.getID());
        assertFalse(epic.getSubtaskIds().contains(epic.getID()));
    }

    @Test
    void shouldReturnCorrectToString() {
        Epic epic = new Epic(1, "Epic1", "Epic desc", Status.NEW);
        epic.addSubtaskId(42);
        String expected = "Epic{id=1, name='Epic1', description='Epic desc', status=NEW, subtaskIds=[42]}";
        assertEquals(expected, epic.toString());
    }
}