package service;

import model.*;
import java.util.List;

public interface HistoryManager {
    void addTask(Task task);
    List<Task> getHistory();
}
