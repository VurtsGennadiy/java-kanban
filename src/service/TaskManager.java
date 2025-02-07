package service;

import model.*;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    void clearAllTasks();

    void clearAllEpics();

    void clearAllSubtasks();

    Task getTask(Integer id);

    Epic getEpic(Integer id);

    Subtask getSubtask(Integer id);

    List<Subtask> getSubtasksOfEpic(Integer epicId);

    Task createNewTask(Task task);

    Epic createNewEpic(Epic epic);

    Subtask createNewSubtask(Subtask subtask);

    void updateTask(Task updatedTask);

    void updateSubtask(Subtask updatedSubtask);

    void updateEpic(Epic updatedEpic);

    void removeTask(Integer id);

    void removeSubtask(Integer id);

    void removeEpic(Integer id);

    List<Task> getHistory();

    SortedSet<Task> getPrioritizedTasks();
}
