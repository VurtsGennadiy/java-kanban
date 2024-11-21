package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    private int idCounter;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
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
        Task task = tasks.get(id);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public Task createNewTask(Task task) {
        task.setId(idCounter);
        tasks.put(idCounter, task);
        updateIdCounter();
        return task;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        epic.setId(idCounter);
        epics.put(idCounter, epic);
        updateIdCounter();
        return epic;
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        subtask.setId(idCounter);
        subtasks.put(idCounter, subtask);
        updateIdCounter();

        Epic epic = subtask.getEpic();
        if (epic != null) {
            epic.addSubtask(subtask);
        }
        return subtask;
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
                    Subtask updTask = (Subtask) updatedTask;
                    Subtask oldSubtask = subtasks.get(updTask.getId());
                    subtasks.put(id, updTask);
                    Epic epic = updTask.getEpic();
                    if (epic != null) {
                        epic.updateSubtask(oldSubtask, updTask);
                        checkEpicStatus(epic.getId());
                    }
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
    public ArrayList<Subtask> getSubtasksOfEpic(Integer epicId) {
        return epics.get(epicId).getSubtasks();
    }

    private void updateIdCounter() {
        idCounter++;
    }

    protected void checkEpicStatus(Integer id) {
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
