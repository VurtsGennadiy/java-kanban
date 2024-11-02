package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int idCounter;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearAllTasks() {
        tasks.clear();
    }

    public void clearAllEpics() {
        epics.clear();
    }

    public void clearAllSubtasks() {
        subtasks.clear();
    }

    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpic(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtask(Integer id) {
        return subtasks.get(id);
    }

    public void createNewTask(Task task) {
        task.setId(idCounter);
        tasks.put(idCounter, task);
        updateId();
    }

    public void createNewEpic(Epic epic) {
        epic.setId(idCounter);
        epics.put(idCounter, epic);
        updateId();
    }

    public void createNewSubtask(Subtask subtask) {
        subtask.setId(idCounter);
        subtasks.put(idCounter, subtask);
        updateId();
    }

    public void updateTask(Task newTask) {
        Integer id = newTask.getId();
        if (newTask.getClass() == Task.class && tasks.containsKey(id)) {
            tasks.put(id, newTask);
        } else if (newTask.getClass() == Subtask.class && subtasks.containsKey(id)) {
            subtasks.put(id, (Subtask) newTask);
        } else if (newTask.getClass() == Epic.class && epics.containsKey(id)) {
            epics.put(id, (Epic) newTask);
        }
    }

    public void removeTask(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            for (Subtask subtask : epics.get(id).getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
        } else if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtask.getEpic().getSubtasks().remove(subtask);
            subtasks.remove(id);
        }
    }

    private void updateId() {
        idCounter++;
    }
}
