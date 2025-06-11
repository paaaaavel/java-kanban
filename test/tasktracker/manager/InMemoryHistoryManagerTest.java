package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldAddAndReturnHistory() {
        Task task = new Task(1, "Task1", "Desc", Status.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void shouldClearHistory() {
        Task task = new Task(1, "Task1", "Desc", Status.NEW);
        historyManager.add(task);
        historyManager.clearHistory();
        assertTrue(historyManager.getHistory().isEmpty());
    }
}