package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldCreateTask() {
        Task task = taskManager.createTask("Test task", "Test description", Status.NEW);
        assertNotNull(task, "Задача не создана");
        assertEquals(task, taskManager.getTaskById(task.getID()), "Созданная задача не совпадает");
    }

    @Test
    void shouldReturnLastTenViewedTasksInHistory() {
        for (int i = 0; i < 12; i++) {
            Task task = taskManager.createTask("Task " + i, "Desc", Status.NEW);
            taskManager.getTaskById(task.getID());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать 10 элементов");
        assertEquals("Task 2", history.get(0).getName(), "Первым должен быть Task 2");
        assertEquals("Task 11", history.get(9).getName(), "Последним должен быть Task 11");
    }

    @Test
    void shouldReturnInitializedManagers() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Должен возвращаться проинициализированный менеджер");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Должен возвращаться проинициализированный HistoryManager");
    }

    @Test
    void shouldCreateAndGetEpic() {
        Epic epic = taskManager.createEpic("Epic 1", "Epic description");
        assertNotNull(epic, "Эпик не создан");
        Epic retrievedEpic = taskManager.getEpicById(epic.getID());
        assertEquals(epic, retrievedEpic, "Эпик не найден по id");
    }

    @Test
    void shouldCreateAndGetSubtask() {
        Epic epic = taskManager.createEpic("Epic 1", "Epic description");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Subtask description", Status.NEW, epic.getID());
        assertNotNull(subtask, "Подзадача не создана");
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getID());
        assertEquals(subtask, retrievedSubtask, "Подзадача не найдена по id");
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = taskManager.createTask("Task 1", "Desc", Status.NEW);
        int id = task.getID();
        taskManager.deleteTaskById(id);
        assertNull(taskManager.getTaskById(id), "Задача должна быть удалена");
    }

    @Test
    void shouldKeepActualVersionInHistory() {
        Task task = taskManager.createTask("Task 1", "Desc", Status.NEW);
        taskManager.getTaskById(task.getID()); // В историю

        task.setName("Updated name");
        taskManager.updateTask(task);

        List<Task> history = taskManager.getHistory();
        Task fromHistory = history.get(history.size() - 1);
        assertEquals("Updated name", fromHistory.getName(), "История должна содержать актуальную версию задачи");
    }

    @Test
    void shouldDeleteAllTasks() {
        taskManager.createTask("Task1", "Desc", Status.NEW);
        taskManager.createTask("Task2", "Desc", Status.NEW);
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
    }

    @Test
    void shouldDeleteAllEpicsAndTheirSubtasks() {
        Epic epic = taskManager.createEpic("Epic", "Desc");
        taskManager.createSubtask("Sub", "Desc", Status.NEW, epic.getID());
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        epic.setDescription("Updated Desc");
        taskManager.updateEpic(epic);
        Epic updated = taskManager.getEpicById(epic.getID());
        assertEquals("Updated Desc", updated.getDescription(), "Описание эпика должно обновиться");
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        Subtask subtask = taskManager.createSubtask("Sub1", "Desc", Status.NEW, epic.getID());
        subtask.setName("Updated Sub");
        taskManager.updateSubtask(subtask);
        Subtask updated = taskManager.getSubtaskById(subtask.getID());
        assertEquals("Updated Sub", updated.getName(), "Имя подзадачи должно обновиться");
    }

    @Test
    void historyShouldBeEmptyAfterDeletingAllTasks() {
        Task task = taskManager.createTask("Task1", "Desc", Status.NEW);
        taskManager.getTaskById(task.getID());
        assertFalse(taskManager.getHistory().isEmpty(), "История должна быть не пуста");

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getHistory().isEmpty(), "История должна быть пуста после удаления всех задач");
    }

    @Test
    void shouldDeleteAllSubtasks() {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        taskManager.createSubtask("Sub1", "Desc", Status.NEW, epic.getID());
        taskManager.createSubtask("Sub2", "Desc", Status.NEW, epic.getID());
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
        assertFalse(taskManager.getAllEpics().isEmpty(), "Эпики должны остаться");
    }

    @Test
    void shouldDeleteSubtaskById() {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        Subtask subtask = taskManager.createSubtask("Sub1", "Desc", Status.NEW, epic.getID());
        int subtaskId = subtask.getID();
        taskManager.deleteSubtaskById(subtaskId);
        assertNull(taskManager.getSubtaskById(subtaskId), "Подзадача должна быть удалена");
        assertFalse(epic.getSubtaskIds().contains(subtaskId), "Эпик не должен содержать ссылку на удалённую подзадачу");
    }

    @Test
    void shouldReturnAllTasksEpicsSubtasks() {
        Task task = taskManager.createTask("Task1", "Desc", Status.NEW);
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        Subtask subtask = taskManager.createSubtask("Sub1", "Desc", Status.NEW, epic.getID());
        assertTrue(taskManager.getAllTasks().contains(task), "Список задач должен содержать задачу");
        assertTrue(taskManager.getAllEpics().contains(epic), "Список эпиков должен содержать эпик");
        assertTrue(taskManager.getAllSubtasks().contains(subtask), "Список подзадач должен содержать подзадачу");
    }

    @Test
    void shouldUpdateEpicStatusAccordingToSubtasks() {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        Subtask sub1 = taskManager.createSubtask("Sub1", "Desc", Status.NEW, epic.getID());
        Subtask sub2 = taskManager.createSubtask("Sub2", "Desc", Status.NEW, epic.getID());
        assertEquals(Status.NEW, taskManager.getEpicById(epic.getID()).getStatus(), "Epic должен быть NEW");

        sub1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(sub1);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getID()).getStatus(), "Epic должен быть IN_PROGRESS");

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        taskManager.updateSubtask(sub1);
        taskManager.updateSubtask(sub2);
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getID()).getStatus(), "Epic должен быть DONE");
    }

    @Test
    void shouldUpdateTask() {
        Task task = taskManager.createTask("Task", "Desc", Status.NEW);
        task.setName("Updated");
        taskManager.updateTask(task);
        assertEquals("Updated", taskManager.getTaskById(task.getID()).getName(), "Имя задачи должно обновиться");
    }
}