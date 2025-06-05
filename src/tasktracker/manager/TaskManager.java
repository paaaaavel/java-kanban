package tasktracker.manager;
import tasktracker.model.Epic;
import tasktracker.model.Status;
import tasktracker.model.Subtask;
import tasktracker.model.Task;
import java.util.HashMap;
import java.util.Map;

//хранит все задачи
//управляет созданием задач
//возвращает задачи по ID
//управляет списками подзадач для эпиков

public class TaskManager {
    private int currentId = 0;

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private int generateId(){
        return ++currentId;
    }

    public Task getTask(int id){
        return tasks.get(id);
    }

    public Task createTask(String name, String description, Status status) {
        int id = generateId();
        Task task = new Task(id, name, description, status);
        tasks.put(id, task);
        return task;
    }

    public Epic createEpic(String name, String description) {
        int id = generateId();
        Epic epic = new Epic(id, name, description, Status.NEW);
        epics.put(id, epic);
        return epic;
    }

    public Subtask createSubtask(String name, String description, Status status, int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик с ID " + epicId + " не найден. Подзадача не будет создана.");
            return null;
        }

        int id = generateId();
        Subtask subtask = new Subtask(id, name, description, status, epicId);
        subtasks.put(id, subtask);

        Epic epic = epics.get(epicId);
        epic.addSubtaskId(id);

        return subtask;
    }

}
