package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest
{
    private TaskManager taskManager;

    @BeforeEach
    void setUp()
    {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldCreateTask()
    {
        Task task = taskManager.createTask("Test task", "Test description", Status.NEW);
        assertNotNull(task, "Задача не создана");
        assertEquals(task, taskManager.getTaskById(task.getID()), "Созданная задача не совпадает");
    }

    @Test
    void shouldReturnLastTenViewedTasksInHistory()
    {
        for (int i = 0; i < 12; i++)
        {
            Task task = taskManager.createTask("Task " + i, "Desc", Status.NEW);
            taskManager.getTaskById(task.getID());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать 10 элементов");
        assertEquals("Task 2", history.get(0).getName(), "Первым должен быть Task 2");
        assertEquals("Task 11", history.get(9).getName(), "Последним должен быть Task 11");
    }

    @Test
    void shouldTasksBeEqualIfIdsMatch()
    {
        Task task1 = new Task(1, "Task1", "Desc", Status.NEW);
        Task task2 = new Task(1, "Task2", "Other desc", Status.IN_PROGRESS);
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");

        Epic epic1 = new Epic(1, "Epic1", "Desc", Status.NEW);
        Epic epic2 = new Epic(1, "Epic2", "Other desc", Status.DONE);
        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");

        Subtask subtask1 = new Subtask(1, "Sub1", "Desc", Status.NEW, 10);
        Subtask subtask2 = new Subtask(1, "Sub2", "Other desc", Status.IN_PROGRESS, 20);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void shouldReturnInitializedManagers()
    {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Должен возвращаться проинициализированный менеджер");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Должен возвращаться проинициализированный HistoryManager");
    }

    @Test
    void shouldCreateAndGetEpic()
    {
        Epic epic = taskManager.createEpic("Epic 1", "Epic description");
        assertNotNull(epic, "Эпик не создан");
        Epic retrievedEpic = taskManager.getEpicById(epic.getID());
        assertEquals(epic, retrievedEpic, "Эпик не найден по id");
    }

    @Test
    void shouldCreateAndGetSubtask()
    {
        Epic epic = taskManager.createEpic("Epic 1", "Epic description");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Subtask description", Status.NEW,
                epic.getID());
        assertNotNull(subtask, "Подзадача не создана");
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getID());
        assertEquals(subtask, retrievedSubtask, "Подзадача не найдена по id");
    }

    @Test
    void shouldDeleteTaskById()
    {
        Task task = taskManager.createTask("Task 1", "Desc", Status.NEW);
        int id = task.getID();
        taskManager.deleteTaskById(id);
        assertNull(taskManager.getTaskById(id), "Задача должна быть удалена");
    }

    @Test
    void shouldKeepActualVersionInHistory()
    {
        Task task = taskManager.createTask("Task 1", "Desc", Status.NEW);
        taskManager.getTaskById(task.getID()); // В историю

        // Меняем задачу в менеджере
        task.setName("Updated name");
        taskManager.updateTask(task);

        List<Task> history = taskManager.getHistory();
        Task fromHistory = history.get(history.size() - 1);
        assertEquals("Updated name", fromHistory.getName(),
                "История должна содержать актуальную версию задачи");
    }

    @Test
    void shouldDeleteAllTasks()
    {
        taskManager.createTask("Task1", "Desc", Status.NEW);
        taskManager.createTask("Task2", "Desc", Status.NEW);

        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
    }

    @Test
    void shouldDeleteAllEpicsAndTheirSubtasks()
    {
        Epic epic = taskManager.createEpic("Epic", "Desc");
        taskManager.createSubtask("Sub", "Desc", Status.NEW, epic.getID());

        taskManager.deleteAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
    }

    @Test
    void shouldUpdateEpic()
    {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        epic.setDescription("Updated Desc");
        taskManager.updateEpic(epic);

        Epic updated = taskManager.getEpicById(epic.getID());
        assertEquals("Updated Desc", updated.getDescription(), "Описание эпика должно обновиться");
    }

    @Test
    void shouldUpdateSubtask()
    {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        Subtask subtask = taskManager.createSubtask("Sub1", "Desc", Status.NEW, epic.getID());
        subtask.setName("Updated Sub");
        taskManager.updateSubtask(subtask);

        Subtask updated = taskManager.getSubtaskById(subtask.getID());
        assertEquals("Updated Sub", updated.getName(), "Имя подзадачи должно обновиться");
    }

    @Test
    void historyShouldBeEmptyAfterDeletingAllTasks()
    {
        Task task = taskManager.createTask("Task1", "Desc", Status.NEW);
        taskManager.getTaskById(task.getID());
        assertFalse(taskManager.getHistory().isEmpty(), "История должна быть не пуста");

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getHistory().isEmpty(), "История должна быть пуста после удаления всех задач");
    }
    // Проверка удаления всех подзадач
    @Test
    void shouldDeleteAllSubtasks() {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        taskManager.createSubtask("Sub1", "Desc", Status.NEW, epic.getID());
        taskManager.createSubtask("Sub2", "Desc", Status.NEW, epic.getID());

        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
        // Проверяем, что сами эпики при этом остались
        assertFalse(taskManager.getAllEpics().isEmpty(), "Эпики должны остаться");
    }

    // Проверка удаления подзадачи по id
    @Test
    void shouldDeleteSubtaskById() {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        Subtask subtask = taskManager.createSubtask("Sub1", "Desc", Status.NEW, epic.getID());
        int subtaskId = subtask.getID();

        taskManager.deleteSubtaskById(subtaskId);

        assertNull(taskManager.getSubtaskById(subtaskId), "Подзадача должна быть удалена");
        // Проверяем, что ссылка в эпике тоже удалена
        assertFalse(epic.getSubtaskIds().contains(subtaskId), "Эпик не должен содержать ссылку на удалённую подзадачу");
    }

    // Проверка getAllTasks / getAllEpics / getAllSubtasks
    @Test
    void shouldReturnAllTasksEpicsSubtasks() {
        Task task = taskManager.createTask("Task1", "Desc", Status.NEW);
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        Subtask subtask = taskManager.createSubtask("Sub1", "Desc", Status.NEW, epic.getID());

        assertTrue(taskManager.getAllTasks().contains(task), "Список задач должен содержать задачу");
        assertTrue(taskManager.getAllEpics().contains(epic), "Список эпиков должен содержать эпик");
        assertTrue(taskManager.getAllSubtasks().contains(subtask), "Список подзадач должен содержать подзадачу");
    }

    // Проверка запрета самоссылки для подзадачи

    // Проверка того, что задачи с одинаковым id не конфликтуют
    @Test
    void shouldNotConflictIfSameIdCreatedManually() {
        Task task1 = new Task(1, "Task1", "Desc", Status.NEW);
        Task task2 = new Task(1, "Task2", "Other Desc", Status.DONE);
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны (по equals)");
    }

    // Проверка, что Epic не может содержать самого себя в subtaskIds
    @Test
    void shouldNotAllowEpicToContainItself() {
        Epic epic = taskManager.createEpic("Epic1", "Desc");
        epic.addSubtaskId(epic.getID());

        assertFalse(epic.getSubtaskIds().contains(epic.getID()), "Эпик не должен содержать сам себя в списке подзадач");
    }
    @Test
    void shouldAddAndRemoveSubtaskIdInEpic() {
        Epic epic = new Epic(1, "Epic1", "Description", Status.NEW);
        assertTrue(epic.getSubtaskIds().isEmpty(), "Список подзадач должен быть пустым");

        epic.addSubtaskId(42);
        assertEquals(1, epic.getSubtaskIds().size(), "Подзадача должна добавиться");
        assertEquals(42, epic.getSubtaskIds().get(0));

        epic.removeSubtaskId(42);
        assertTrue(epic.getSubtaskIds().isEmpty(), "Список должен снова стать пустым");
    }
    @Test
    void shouldReturnCorrectEpicId() {
        Subtask subtask = new Subtask(1, "Subtask1", "Desc", Status.NEW, 99);
        assertEquals(99, subtask.getEpicId(), "EpicId должен быть равен 99");
    }
    @Test
    void shouldUpdateTaskFields() {
        Task task = new Task(1, "Old Name", "Old Desc", Status.NEW);

        task.setName("New Name");
        task.setDescription("New Desc");
        task.setStatus(Status.DONE);

        assertEquals("New Name", task.getName());
        assertEquals("New Desc", task.getDescription());
        assertEquals(Status.DONE, task.getStatus());
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
    void shouldUpdateTaskFieldsCorrectly() {
        Task task = new Task(1, "name", "desc", Status.NEW);
        task.setName("new name");
        assertEquals("new name", task.getName());

        task.setDescription("new desc");
        assertEquals("new desc", task.getDescription());

        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void shouldReturnCorrectToString() {
        Task task = new Task(1, "name", "desc", Status.NEW);
        String expected = "Task{id=1, name='name', description='desc'}";
        assertEquals(expected, task.toString());
    }
    @Test
    void shouldReturnCorrectToStringForEpic() {
        Epic epic = new Epic(1, "Epic1", "Epic desc", Status.NEW);
        epic.addSubtaskId(42);
        String expected = "Epic{id=1, name='Epic1', description='Epic desc', status=NEW, subtaskIds=[42]}";
        assertEquals(expected, epic.toString(), "Метод toString должен возвращать корректную строку");
    }

    @Test
    void shouldReturnCorrectToStringForSubtask() {
        Subtask subtask = new Subtask(1, "Sub1", "Desc", Status.IN_PROGRESS, 99);
        String expected = "Subtask{id=1, name='Sub1', description='Desc', status=IN_PROGRESS, epicId=99}";
        assertEquals(expected, subtask.toString(), "Метод toString должен возвращать корректную строку");
    }

    @Test
    void shouldReturnSubtaskEpicId() {
        Subtask subtask = new Subtask(1, "Sub1", "Desc", Status.NEW, 42);
        assertEquals(42, subtask.getEpicId(), "Метод getEpicId должен возвращать корректный ID эпика");
    }

    @Test
    void shouldGetSubtaskIdsFromEpic() {
        Epic epic = new Epic(1, "Epic1", "Desc", Status.NEW);
        assertTrue(epic.getSubtaskIds().isEmpty(), "Список подзадач должен быть пустым");

        epic.addSubtaskId(42);
        List<Integer> ids = epic.getSubtaskIds();
        assertEquals(1, ids.size(), "Список подзадач должен содержать 1 элемент");
        assertEquals(42, ids.get(0), "ID подзадачи должен быть 42");
    }
    @Test
    void shouldReturnSameHashCodeForSameId() {
        Task task1 = new Task(1, "Task1", "Desc", Status.NEW);
        Task task2 = new Task(1, "Task2", "Other Desc", Status.IN_PROGRESS);

        assertEquals(task1.hashCode(), task2.hashCode(), "Задачи с одинаковым id должны иметь одинаковый hashCode");
    }

    @Test
    void shouldDeleteAllEpics() {
        Epic epic = taskManager.createEpic("Epic", "Desc");
        taskManager.createSubtask("Subtask", "Desc", Status.NEW, epic.getID());
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
    }

    @Test
    void shouldUpdateTask() {
        Task task = taskManager.createTask("Task", "Desc", Status.NEW);
        task.setName("Updated");
        taskManager.updateTask(task);
        assertEquals("Updated", taskManager.getTaskById(task.getID()).getName(), "Имя задачи должно обновиться");
    }
}
