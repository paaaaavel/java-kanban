package tasktracker.main;

import tasktracker.manager.Managers;
import tasktracker.manager.TaskManager;
import tasktracker.model.Epic;
import tasktracker.model.Status;
import tasktracker.model.Subtask;
import tasktracker.model.Task;


public class Main
{

    public static void main(String[] args)
    {
        TaskManager manager = Managers.getDefault();
        // Создаём обычные задачи
        Task task1 = manager.createTask("Полить цветы", "Набрать воды в кувшин и полить цветы",
                Status.NEW);
        Task task2 = manager.createTask("Сделать коммит проекта",
                "Прописать коммит проекта в консоли и запушить", Status.NEW);

        // Создаём эпики
        Epic epic1 = manager.createEpic("Купить квартиру", "Подготовка к переезду");
        Epic epic2 = manager.createEpic("Закончить курс Java-Developer", "Получить диплом");

        // Создаём подзадачи для эпиков
        Subtask subtask1 = manager.createSubtask("Выбрать район",
                "Рассмотреть районы для покупки", Status.NEW, epic1.getID());
        Subtask subtask2 = manager.createSubtask("Нанять риэлтора",
                "Рассмотреть варианты которые предлагает риэлтор", Status.NEW, epic1.getID());
        Subtask subtask3 = manager.createSubtask("Сдать итоговый проект",
                "Завершить проект и отправить на проверку", Status.NEW, epic2.getID());

        // Выводим все задачи, эпики и подзадачи
        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());
        System.out.println("Все эпики:");
        System.out.println(manager.getAllEpics());
        System.out.println("Все подзадачи:");
        System.out.println(manager.getAllSubtasks());

        // Изменяем статусы подзадач
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        // Смотрим, как пересчитался статус эпика
        System.out.println("\nПосле обновления подзадач:");
        System.out.println(manager.getAllEpics());

        // Удаляем одну задачу и один эпик
        manager.deleteTaskById(task1.getID());
        manager.deleteEpicById(epic1.getID());

        // Выводим финальное состояние всех списков
        System.out.println("\nПосле удаления задачи и эпика:");
        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());
        System.out.println("Все эпики:");
        System.out.println(manager.getAllEpics());
        System.out.println("Все подзадачи:");
        System.out.println(manager.getAllSubtasks());
    }
}