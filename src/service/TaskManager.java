package service;

import model.*;
import java.util.ArrayList;

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
    ArrayList<Subtask> getSubtasksOfEpic(Integer id);

    void createNewTask(Task task);
    void createNewEpic(Epic epic);
    void createNewSubtask(Subtask subtask);

    void updateTask(Task updatedTask);
    void removeTask(Integer id);
}
