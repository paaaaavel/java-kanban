package tasktracker.manager;

import tasktracker.model.Status;
import tasktracker.model.Task;
import tasktracker.model.Subtask;
import tasktracker.model.Epic;

import java.util.List;

public interface TaskManager
{
    Task createTask(String name, String description, Status status);

    Epic createEpic(String name, String description);

    Subtask createSubtask(String name, String description, Status status, int epicId);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getHistory();
}