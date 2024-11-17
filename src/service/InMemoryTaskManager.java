package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private static int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtasks.clear();
        for (Epic epic : getAllEpics()) {
            epic.getSubtasks().clear();
            checkEpicStatus(epic.getId());
        }
    }

    @Override
    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(Integer id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(Integer id) {
        return subtasks.get(id);
    }

    @Override
    public void createNewTask(Task task) {
        task.setId(idCounter);
        tasks.put(idCounter, task);
        updateIdCounter();
    }

    @Override
    public void createNewEpic(Epic epic) {
        epic.setId(idCounter);
        epics.put(idCounter, epic);
        updateIdCounter();
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        subtask.setId(idCounter);
        subtasks.put(idCounter, subtask);
        updateIdCounter();
    }

    @Override
    public void updateTask(Task updatedTask) {
        Integer id = updatedTask.getId();
        TaskType taskType = updatedTask.getType();
        switch (taskType) {
            case TASK:
                if (tasks.containsKey(id)) {
                    tasks.put(id, updatedTask);
                }
                break;
            case EPIC:
                if (epics.containsKey(id)) {
                    epics.put(id, (Epic) updatedTask);
                }
                break;
            case SUBTASK:
                if (subtasks.containsKey(id)) {
                    subtasks.put(id, (Subtask) updatedTask);
                    checkEpicStatus(((Subtask) updatedTask).getEpic().getId());
                }
                break;
        }
    }

    @Override
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

    @Override
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
        int subtasksDoneStatusCounter = 0;
        for (Subtask subtask : epic.getSubtasks()) {
            if (TaskStatus.NEW == subtask.getStatus()) {
                subtasksNewStatusCounter++;
            } else if (TaskStatus.DONE == subtask.getStatus()) {
                subtasksDoneStatusCounter++;
            } else break;
        }
        if (epic.getSubtasks().isEmpty() || subtasksNewStatusCounter == epic.getSubtasks().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subtasksDoneStatusCounter == epic.getSubtasks().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
