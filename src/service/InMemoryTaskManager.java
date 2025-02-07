package service;

import model.*;
import exceptions.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTaskSet;
    protected int idCounter;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTaskSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        idCounter = 1;
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
        tasks.keySet().forEach(historyManager::remove);
        tasks.values().forEach(prioritizedTaskSet::remove);
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        Stream.concat(epics.keySet().stream(), subtasks.keySet().stream()).forEach(historyManager::remove);
        subtasks.values().forEach(prioritizedTaskSet::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(prioritizedTaskSet::remove);
        subtasks.values().forEach(subtask -> this.getEpic(subtask.getEpicId()).removeSubtask(subtask));
        subtasks.clear();
        epics.keySet().forEach(this::checkEpicStatus);
    }

    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new TaskNotFoundException("Задача с id = " + id + " не найдена.");
        }
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new TaskNotFoundException("Эпик с id = " + id + " не найден.");
        }
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new TaskNotFoundException("Подзадача с id = " + id + " не найдена.");
        }
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public Task createNewTask(Task task) {
        if (hasIntersect(task)) {
            throw new TaskHasIntersectException("Задача пересекается по времени с другой задачей");
        }
        task.setId(idCounter);
        tasks.put(idCounter, task);
        updateIdCounter();
        if (task.getStartTime() != null) {
            prioritizedTaskSet.add(task);
        }
        return task;
    }

    // полагаем что эпик приходит пустой
    @Override
    public Epic createNewEpic(Epic epic) {
        if (!epic.getSubtasksId().isEmpty()) {
            throw new ManagerCreateTaskException("Эпик не должен содержать подзадачи");
        }
        epic.setId(idCounter);
        epics.put(idCounter, epic);
        updateIdCounter();
        return epic;
    }

    // для подзадачи должен быть задан эпик
    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new ManagerCreateTaskException("Эпик подзадачи не существует в менеджере");
        }
        if (hasIntersect(subtask)) {
            throw new TaskHasIntersectException("Подзадача пересекается по времени с другой задачей");
        }
        subtask.setId(idCounter);
        subtasks.put(idCounter, subtask);
        updateIdCounter();
        epic.addSubtask(subtask);
        checkEpicStatus(epic.getId());
        if (subtask.getStartTime() != null) {
            prioritizedTaskSet.add(subtask);
        }
        return subtask;
    }

    @Override
    public void updateTask(Task updatedTask) {
        Integer id = updatedTask.getId();
        if (tasks.containsKey(id)) {
            if (updatePrioritizedTaskSet(updatedTask)) {
                tasks.put(id, updatedTask);
            }
            else {
                throw new TaskHasIntersectException("Задача пересекается во времени с существующими задачами");
            }
        } else {
            throw new TaskNotFoundException("Подзадача с id = " + id + " не найдена.");
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        Integer id = updatedSubtask.getId();
        if (subtasks.containsKey(id)) {
            if (updatePrioritizedTaskSet(updatedSubtask)) {
                subtasks.put(id, updatedSubtask);
                checkEpicStatus(updatedSubtask.getEpicId());
            }
            else {
                throw new TaskHasIntersectException("Подзадача пересекается во времени с существующими задачами");
            }
        } else {
            throw new TaskNotFoundException("Подзадача с id = " + id + " не найдена.");
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Integer id = updatedEpic.getId();
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            // обновляем только поля name и description
            // список подзадач остаётся нетронутый
            epic.setName(updatedEpic.getName());
            epic.setDescription(updatedEpic.getDescription());
        } else {
            throw new TaskNotFoundException("Эпик с id = " + id + " не найден.");
        }
    }

    @Override
    public void removeTask(Integer id) {
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Задача с id = " + id + " не найдена.");
        }
        removeFromPrioritizedTaskSet(tasks.get(id));
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(Integer id) {
        if (!subtasks.containsKey(id)) {
            throw new TaskNotFoundException("Подзадача с id = " + id + " не найдена.");
        }
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtask);
        checkEpicStatus(epic.getId());
        removeFromPrioritizedTaskSet(subtasks.get(id));
        historyManager.remove(id);
        subtasks.remove(id);
    }

    @Override
    public void removeEpic(Integer id) {
        if (!epics.containsKey(id)) {
            throw new TaskNotFoundException("Эпик с id = " + id + " не найден.");
        }
        Epic epic = epics.get(id);
        epic.getSubtasksId().forEach(subtaskId -> {
            historyManager.remove(subtaskId);
            removeFromPrioritizedTaskSet(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
        });
        historyManager.remove(epic.getId());
        epics.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Integer epicId) {
        if (!epics.containsKey(epicId)) {
            throw new TaskNotFoundException("Эпик с id = " + epicId + " не найден.");
        }
        return epics.get(epicId).getSubtasksId().stream()
                .map(subtasks::get)
                .toList();
    }

    private void updateIdCounter() {
        idCounter++;
    }

    private void checkEpicStatus(Integer id) {
        Epic epic = epics.get(id);
        if (epic == null) return;
        List<Integer> subtasksId = epic.getSubtasksId();
        Map<TaskStatus, List<Subtask>> groupedSubtasks = subtasksId.stream().map(subtasks::get)
                .collect(Collectors.groupingBy(Task::getStatus));
        int subtasksNewStatusCount = groupedSubtasks.getOrDefault(TaskStatus.NEW, Collections.emptyList()).size();
        int subtasksDoneStatusCount = groupedSubtasks.getOrDefault(TaskStatus.DONE, Collections.emptyList()).size();

        if (epic.getSubtasksId().isEmpty() || subtasksNewStatusCount == epic.getSubtasksId().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subtasksDoneStatusCount == epic.getSubtasksId().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public SortedSet<Task> getPrioritizedTasks() {
        return Collections.unmodifiableSortedSet(prioritizedTaskSet);
    }

    private boolean updatePrioritizedTaskSet(Task updatedTask) {
        int id = updatedTask.getId();
        Task taskForRemove = tasks.getOrDefault(id, subtasks.get(id));
        if (taskForRemove != null && taskForRemove.getStartTime() != null) {
            prioritizedTaskSet.remove(taskForRemove);
            if (hasIntersect(updatedTask)) {
                prioritizedTaskSet.add(taskForRemove);
                return false;
            }
        }
        if (updatedTask.getStartTime() != null) {
            prioritizedTaskSet.add(updatedTask);
        }
        return true;
    }

    private void removeFromPrioritizedTaskSet(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        prioritizedTaskSet.remove(task);
    }

    private boolean hasIntersect(Task task) {
        if (task instanceof Epic) {
            throw new IllegalArgumentException("Операция не предусмотрена для класса эпик");
        }
        return getPrioritizedTasks().stream().anyMatch(other -> other.isIntersect(task));
    }
}
