package tasktracker.manager;

import tasktracker.model.Epic;
import tasktracker.model.Status;
import tasktracker.model.Subtask;
import tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//хранит все задачи
//управляет созданием задач
//возвращает задачи по ID
//управляет списками подзадач для эпиков
public class InMemoryTaskManager implements TaskManager
{
    private int currentId = 0;
    private final HistoryManager historyManager;

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public InMemoryTaskManager(HistoryManager historyManager)
    {
        this.historyManager = historyManager;
    }

    private int generateId()
    {
        return ++currentId;
    }

    public Task createTask(String name, String description, Status status)
    {
        int id = generateId();
        Task task = new Task(id, name, description, status);
        tasks.put(id, task);
        return task;
    }

    public Epic createEpic(String name, String description)
    {
        int id = generateId();
        Epic epic = new Epic(id, name, description, Status.NEW);
        epics.put(id, epic);
        return epic;
    }

    public Subtask createSubtask(String name, String description, Status status, int epicId)
    {
        if (!epics.containsKey(epicId))
        {
            System.out.println("Эпик с ID " + epicId + " не найден. Подзадача не будет создана.");
            return null;
        }

        // Проверка: подзадача не может ссылаться на себя как на эпик
        if (epicId == currentId + 1) {
            System.out.println("Ошибка: Подзадача не может ссылаться на себя как на эпик.");
            return null;
        }

        int id = generateId();
        Subtask subtask = new Subtask(id, name, description, status, epicId);
        subtasks.put(id, subtask);

        Epic epic = epics.get(epicId);
        epic.addSubtaskId(id);

        return subtask;
    }

    public ArrayList<Task> getAllTasks()
    {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics()
    {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks()
    {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllTasks()
    {
        tasks.clear();
    }

    public void deleteAllEpics()
    {
        deleteAllSubtasks();
        epics.clear();
    }

    public void deleteAllSubtasks()
    {
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id)
    {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id)
    {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id)
    {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    public void deleteTaskById(int id)
    {
        tasks.remove(id);
    }

    public void deleteEpicById(int id)
    {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtaskIds())
        {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(int id)
    {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        subtasks.remove(id);
        epic.removeSubtaskId(id);
    }

    public void updateTask(Task task)
    {
        tasks.put(task.getID(), task);
    }

    public void updateEpic(Epic epic)
    {
        epics.put(epic.getID(), epic);

    }

    public void updateSubtask(Subtask subtask)
    {
        subtasks.put(subtask.getID(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public List<Task> getHistory()
    {
        return historyManager.getHistory();
    }

    public void updateEpicStatus(int id)
    {
        int countInProgress = 0;
        int countDone = 0;
        int countNew = 0;
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtaskIds())
        {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus().equals(Status.IN_PROGRESS))
            {
                countInProgress++;
            } else if (subtask.getStatus().equals(Status.DONE))
            {
                countDone++;
            } else if (subtask.getStatus().equals(Status.NEW))
            {
                countNew++;
            }
        }
        if (epic.getSubtaskIds().isEmpty())
        {
            epic.setStatus(Status.NEW);
        } else if (countDone == epic.getSubtaskIds().size())
        {
            epic.setStatus(Status.DONE);
        } else if (countNew == epic.getSubtaskIds().size())
        {
            epic.setStatus(Status.NEW);
        } else
        {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
