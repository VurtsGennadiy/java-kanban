package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int HISTORY_LIST_SIZE = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>(HISTORY_LIST_SIZE);
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        history.add(task);
        if (history.size() > HISTORY_LIST_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }
}
