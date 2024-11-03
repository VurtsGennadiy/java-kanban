package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

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
        updateIdCounter();
    }

    public void createNewEpic(Epic epic) {
        epic.setId(idCounter);
        epics.put(idCounter, epic);
        updateIdCounter();
    }

    public void createNewSubtask(Subtask subtask) {
        subtask.setId(idCounter);
        subtasks.put(idCounter, subtask);
        updateIdCounter();
    }

    public void updateTask(Task updatedTask) {
        Integer id = updatedTask.getId();
        if (updatedTask.getClass() == Task.class && tasks.containsKey(id)) {
            tasks.put(id, updatedTask);
        } else if (updatedTask.getClass() == Subtask.class && subtasks.containsKey(id)) {
            subtasks.put(id, (Subtask) updatedTask);
            checkEpicStatus(((Subtask) updatedTask).getEpic().getId());
        } else if (updatedTask.getClass() == Epic.class && epics.containsKey(id)) {
            epics.put(id, (Epic) updatedTask);
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
            checkEpicStatus(subtask.getEpic().getId());
            subtasks.remove(id);
        }
    }

    public ArrayList<Subtask> getSubtasksOfEpic(Integer id) {
        return epics.get(id).getSubtasks();
    }

    private void updateIdCounter() {
        idCounter++;
    }

    private void checkEpicStatus(Integer id) {
        Epic epic = epics.get(id);
        if (epic == null) return;

        int subtasksNewStatusCounter = 0;
        int subtasksDoneStatusCouner = 0;
        for (Subtask subtask : epic.getSubtasks()) {
            if (TaskStatus.NEW == subtask.getStatus()) {
                subtasksNewStatusCounter++;
            } else if (TaskStatus.DONE == subtask.getStatus()) {
                subtasksDoneStatusCouner++;
            } else break;
        }
        if (epic.getSubtasks().isEmpty() || subtasksNewStatusCounter == epic.getSubtasks().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subtasksDoneStatusCouner == epic.getSubtasks().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
