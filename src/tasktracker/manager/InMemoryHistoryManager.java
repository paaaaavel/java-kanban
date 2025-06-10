package tasktracker.manager;

import tasktracker.model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private  List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        history.add(task);

        // Если размер истории больше 10 — удаляем первый (старый) элемент
        if (history.size() > 10) {
            history.remove(0);
        }
    }
    @Override
    public void clearHistory() {
        history.clear();
    }
    @Override
    public List<Task> getHistory() {
        return history;
    }
}