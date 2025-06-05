package tasktracker.main;

import tasktracker.model.Status;
import tasktracker.model.Task;
import tasktracker.manager.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<Task> tasks = new ArrayList<>();
        TaskManager maneger = new TaskManager();
        Task task1 = maneger.createTask("Налить воды", "Взять кувшин, налить туда воды из крана", Status.NEW);
        Task task2 = maneger.createTask("Налить воды", "Взять кувшин, налить туда воды из крана", Status.NEW);
        Task task3 = maneger.createTask("Налить воды", "Взять кувшин, налить туда воды из крана", Status.NEW);
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        printTasks(tasks);
    }


    public static void printTasks(ArrayList<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
