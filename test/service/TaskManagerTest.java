package service;

import exceptions.ManagerCreateTaskException;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import java.util.SortedSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    Task task;
    Subtask subtask;
    Epic epic;

    @BeforeEach
    void init() {
        task = new Task("Task1_Name","Task1_Description",
                LocalDateTime.of(2025, 1,1,8,0), Duration.ofHours(1));
        epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask");
        subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1",
                LocalDateTime.of(2025,1,2,10,0), Duration.ofMinutes(30), epic);
    }

    @Test
    void createNewTaskAndFindById() {
        manager.createNewTask(task);
        assertEquals(1, manager.getAllTasks().size(), "Не добавился task");
        assertSame(task, manager.getTask(task.getId()), "Не находит task по id");
        assertSame(task, manager.getPrioritizedTasks().getFirst(), "task не добавился в сортированный список");
    }

    @Test
    void createNewSubtaskAndFindById() {
        manager.createNewEpic(epic);
        assertEquals(1, manager.getAllSubtasks().size(), "Не добавился subtask");
        assertSame(subtask, manager.getSubtask(subtask.getId()), "Не находит subtask по id");
        assertSame(subtask, manager.getPrioritizedTasks().getFirst(),
                "subtask не добавился в сортированный список");
    }

    @Test
    void createNewEpicAndFindById() {
        manager.createNewEpic(epic);
        SortedSet<Task> sortedSet = manager.getPrioritizedTasks();

        assertEquals(1, manager.getAllEpics().size(), "Не добавился epic");
        assertEquals(1, manager.getAllSubtasks().size(), "Subtask из эпика не добавился в менеджер");
        assertSame(epic, manager.getEpic(epic.getId()), "Не находит epic по id");
        assertEquals(1, sortedSet.size(), "В sortedSet должен попасть только subtask");
        assertSame(subtask, sortedSet.getFirst());
    }

    @Test
    void createNewEpicContainsSubtasks() {
        Subtask subtask2 = new Subtask("","", epic);
        Subtask subtask3 = new Subtask("","", epic);

        manager.createNewEpic(epic);

        assertEquals(3, epic.getSubtasks().size(), "Subtasks не добавились в эпик");
        assertEquals(3, manager.getAllSubtasks().size(), "Subtasks не добавились в менеджер");
    }

    @Test
    void createNewTask_shouldNoChangeTaskFields() {
        Task addedTask = new Task(task.getName(), task.getDescription());
        addedTask.setStatus(task.getStatus());
        addedTask = manager.createNewTask(addedTask);

        assertEquals(task.getName(), addedTask.getName(), "Изменилось поле Name");
        assertEquals(task.getDescription(), addedTask.getDescription(), "Изменилось поле Description");
        assertEquals(task.getStatus(), addedTask.getStatus(), "Изменилось поле Status");
    }

    @Test
    void createNewSubtask_shouldNoChangeSubtaskFields() {
        manager.createNewEpic(epic);
        Subtask addedSubtask = new Subtask(subtask.getName(), subtask.getDescription(), epic);
        addedSubtask.setStatus(subtask.getStatus());
        addedSubtask = manager.createNewSubtask(addedSubtask);

        assertEquals(subtask.getName(), addedSubtask.getName(), "Изменилось поле Name");
        assertEquals(subtask.getDescription(), addedSubtask.getDescription(), "Изменилось поле Description");
        assertEquals(subtask.getStatus(), addedSubtask.getStatus(), "Изменилось поле Status");
        assertSame(subtask.getEpic(), addedSubtask.getEpic(), "Изменилось поле Epic");
    }

    @Test
    void createNewEpic_shouldNoChangeEpicFields() {
        Epic addedEpic = new Epic(epic.getName(), epic.getDescription(), epic.getSubtasks());
        addedEpic.setStatus(epic.getStatus());
        addedEpic = manager.createNewEpic(addedEpic);

        assertEquals(epic.getName(), addedEpic.getName(), "Изменилось поле Name");
        assertEquals(epic.getDescription(), addedEpic.getDescription(), "Изменилось поле Description");
        assertEquals(epic.getStatus(), addedEpic.getStatus(), "Изменилось поле Status");
        assertEquals(epic.getSubtasks(), addedEpic.getSubtasks(), "Изменился список subtasks");
    }

    @Test
    void updateTask() {
        Task task = manager.createNewTask(this.task);
        Task updTask = new Task(task.getName(), "updatedTask",
                LocalDateTime.of(2025,1,3,0,0), Duration.ofHours(1));
        updTask.setId(task.getId());
        manager.updateTask(updTask);

        assertEquals(1, manager.getAllTasks().size(), "Должен остаться 1 task");
        assertNotSame(task, manager.getTask(task.getId()), "Task не заменился в менеджере");
        assertEquals(1, manager.getPrioritizedTasks().size(),
                "В сортированном списке должен остаться 1 task");
        assertSame(updTask, manager.getPrioritizedTasks().getFirst(), "task не заменился в prioritizedTaskSet");
    }

    @Test
    void updateEpic() {
        Epic epic = manager.createNewEpic(this.epic);
        Epic updEpic = new Epic(epic.getName(), "updatedEpic");
        updEpic.setId(epic.getId());
        manager.updateTask(updEpic);

        assertEquals(1, manager.getAllEpics().size(), "Должен остаться 1 epic");
        assertNotSame(epic, manager.getEpic(epic.getId()), "Epic не заменился в менеджере");
    }

    @Test
    void updateSubtask() {
        Epic epic = manager.createNewEpic(this.epic);

        Subtask updSubtask = new Subtask(subtask.getName(), "updatedSubtask",
                LocalDateTime.of(2025,1,3,0,0), Duration.ofHours(1));
        updSubtask.setId(subtask.getId());
        updSubtask.setStatus(TaskStatus.IN_PROGRESS);
        updSubtask.setEpic(epic);
        manager.updateTask(updSubtask);

        assertEquals(1, manager.getAllSubtasks().size(),
                "В менеджере должен остаться один subtask");
        assertNotSame(subtask, manager.getSubtask(subtask.getId()), "Subtask не обновился");
        assertEquals(1, epic.getSubtasks().size(), "В эпике должен остаться один Subtask");
        assertSame(updSubtask, epic.getSubtasks().getFirst(), "В эпике не обновился Subtask");
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Epic status не обновился");
        assertEquals(1, manager.getPrioritizedTasks().size(),
                "В сортированном списке должен остаться 1 task");
        assertSame(updSubtask, manager.getPrioritizedTasks().getFirst(),
                "task не заменился в prioritizedTaskSet");
    }

    @Test
    void getSubtasksOfEpic() {
        Epic epic = manager.createNewEpic(this.epic);
        List<Subtask> subtasks = manager.getSubtasksOfEpic(epic.getId());
        assertSame(subtask, subtasks.getFirst(), "Subtask не добавился в Epic");
    }

    @Test
    void removeTaskById() {
        manager.createNewTask(task);
        int taskId = task.getId();

        manager.getTask(taskId);
        manager.removeTask(taskId);

        assertNull(manager.getTask(taskId), "task не удалился из taskManager");
        assertEquals(Collections.emptyList(), manager.getHistory(), "task не удалился из historyManager");
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Не очистился сортированный список");
    }

    @Test
    void removeSubtaskById() {
        manager.createNewEpic(epic);
        int subtaskId = subtask.getId();

        manager.getSubtask(subtaskId);
        manager.removeTask(subtaskId);

        assertNull(manager.getSubtask(subtaskId), "subtask не удалился из taskManager");
        assertTrue(epic.getSubtasks().isEmpty(), "subtask не удалился из epic");
        assertEquals(Collections.emptyList(), manager.getHistory(), "subtask не удалился из historyManager");
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Не очистился сортированный список");
    }

    @Test
    void removeEpicById() {
        manager.createNewEpic(epic);

        manager.getSubtask(subtask.getId());
        manager.getEpic(epic.getId());
        manager.removeTask(epic.getId());

        assertNull(manager.getEpic(epic.getId()), "Не удалился epic");
        assertNull(manager.getSubtask(subtask.getId()), "Не удалился subtask связанный с epic");
        assertEquals(Collections.emptyList(), manager.getHistory(), "Не очистилась история");
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Не очистился сортированный список");
    }

    @Test
    void clearAllTasks() {
        manager.createNewTask(task);
        manager.getTask(task.getId());
        manager.clearAllTasks();

        assertTrue(manager.getAllTasks().isEmpty(), "Не очистился список tasks в менеджере");
        assertFalse(manager.getHistory().contains(task), "Task не удалился из истории");
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Не очистился сортированный список");
    }

    @Test
    void clearAllEpics() {
        manager.createNewEpic(epic);
        manager.getSubtask(subtask.getId());
        manager.getEpic(epic.getId());
        manager.clearAllEpics();

        assertTrue(manager.getAllEpics().isEmpty(), "Не очистился список epics в менеджере");
        assertTrue(manager.getAllSubtasks().isEmpty(),
                "Не очистился список subtasks в менеджере, subtask не должен существовать отдельно от epic");
        assertFalse(manager.getHistory().contains(epic), "Epic не удалился из истории");
        assertFalse(manager.getHistory().contains(subtask),
                "Subtask, принадлежащий epic не удалился из истории");
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Не очистился сортированный список");
    }

    @Test
    void clearAllSubtasks() {
        manager.createNewEpic(epic);
        manager.getSubtask(subtask.getId());
        manager.getEpic(epic.getId());
        manager.clearAllSubtasks();

        assertTrue(manager.getAllSubtasks().isEmpty(), "Не очистился список subtasks в менеджере");
        assertFalse(manager.getAllEpics().isEmpty(),
                "Epic, связанный с subtask не должен удалиться из менеджера");
        assertTrue(epic.getSubtasks().isEmpty(), "Не очистился список subtasks в epic");
        assertFalse(manager.getHistory().contains(subtask), "Subtask не удалился из истории");
        assertTrue(manager.getHistory().contains(epic),
                "Epic, связанный с subtask не должен удалиться истории");
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Не очистился сортированный список");
    }

    @Test
    void shouldAddTaskWithSettingIdWhenContainSameId() {
        Task taskWithGenerateId = manager.createNewTask(task);
        int id = taskWithGenerateId.getId();
        Task taskWithSetId = new Task(task.getName(), task.getDescription());
        taskWithSetId.setId(id);
        taskWithSetId = manager.createNewTask(taskWithSetId);

        assertEquals(2, manager.getAllTasks().size(), "Task не добавился в manager");
        assertNotEquals(taskWithGenerateId.getId(), taskWithSetId.getId(), "Совпали Id у двух task");
    }

    @Test
    void epicStatusNewWhenSubtasksNew() {
        manager.createNewEpic(epic);
        Subtask subtask2 = new Subtask("subtask2_name", "subtask2_description", epic);
        manager.createNewSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус должен быть NEW");
    }

    @Test
    void epicStatusNewWhenSubtasksDone() {
        manager.createNewEpic(epic);
        Subtask subtask2 = new Subtask("subtask2_name", "subtask2_description", epic);
        subtask.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        manager.createNewSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус должен быть DONE");
    }

    @Test
    void epicStatusNewWhenSubtasksNewAndDone() {
        manager.createNewEpic(epic);
        subtask.setStatus(TaskStatus.DONE);
        Subtask subtask2 = new Subtask("subtask2_name", "subtask2_description", epic);
        manager.createNewSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS");
    }

    @Test
    void epicStatusNewWhenSubtasksInProgress() {
        manager.createNewEpic(epic);
        Subtask subtask2 = new Subtask("subtask2_name", "subtask2_description", epic);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.createNewSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS");
    }


    @Test
    void getPrioritizedTasks() {
        manager.createNewTask(task);
        manager.createNewEpic(epic);
        manager.createNewTask(new Task("NullStartTimeTask",""));

        SortedSet<Task> sortedSet = manager.getPrioritizedTasks();
        assertEquals(2, sortedSet.size(), "В сортированном списке лишние задачи");
        assertSame(task, sortedSet.getFirst(), "Нарушен порядок сортировки");

        manager.removeTask(subtask.getId());
        Task updTask = new Task("updatedTask","", LocalDateTime.now(), Duration.ofHours(1));
        updTask.setId(task.getId());
        manager.updateTask(updTask);
        sortedSet = manager.getPrioritizedTasks();
        assertEquals(1, sortedSet.size(), "Задачи не удалились из сортированного списка");
        assertSame(updTask, sortedSet.getFirst(), "Задача не обновилась в сортированном списке");
    }

    @Test
    void shouldNotAddNullStartTimeTaskInPrioritizedSetWhenUpdateTask() {
        manager.createNewTask(task);
        manager.createNewEpic(epic);

        Task taskForUpdate = new Task();
        Subtask subtaskForUpdate = new Subtask();
        taskForUpdate.setId(task.getId());
        subtaskForUpdate.setId(subtask.getId());
        subtaskForUpdate.setEpic(epic);
        manager.updateTask(taskForUpdate);
        manager.updateTask(subtaskForUpdate);
        SortedSet<Task> sortedSet = manager.getPrioritizedTasks();

        assertTrue(sortedSet.isEmpty(),
                "Задачи с не заданным startTime не должны попасть в prioritizedTaskSet");
        assertEquals(1, manager.getAllTasks().size());
        assertSame(taskForUpdate, manager.getAllTasks().getFirst());
        assertEquals(1, manager.getAllSubtasks().size());
        assertSame(subtaskForUpdate, manager.getAllSubtasks().getFirst());
    }

    @Test
    @DisplayName("Не добавлять задачу, если есть пересечение по времени")
    void shouldNoCreateTaskWhenHasIntersect() {
        LocalDateTime startTime = LocalDateTime.of(2025,1,1,10,0);
        Task task = new Task("", "", startTime, Duration.ofHours(4));
        Task taskWithIntersect = new Task("", "", startTime, Duration.ofHours(1));
        manager.createNewTask(task);
        assertThrows(ManagerCreateTaskException.class, () -> manager.createNewTask(taskWithIntersect));

        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    @DisplayName("Не добавлять эпик, если у его сабтаски есть пересечение")
    void shouldNoCreateEpicWhenHerSubtaskHasIntersect() {
        Subtask sub2 = new Subtask("","", task.getStartTime(), Duration.ofHours(1), epic);
        manager.createNewTask(task);

        assertThrows(ManagerCreateTaskException.class, () -> manager.createNewEpic(epic));
        assertEquals(2, epic.getSubtasks().size());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldNoUpdateTaskWhenHasIntersect() {
        LocalDateTime startTime = LocalDateTime.of(2025,1,1,10,0);
        Task task1 = new Task("", "", startTime, Duration.ofHours(4));
        Task task2 = new Task("","", task1.getEndTime(), Duration.ofHours(4));
        Task taskForUpdate = new Task("","", task2.getStartTime(), Duration.ofHours(1));
        manager.createNewTask(task1);
        manager.createNewTask(task2);
        taskForUpdate.setId(task1.getId());
        manager.updateTask(taskForUpdate);

        assertTrue(task2.isIntersect(taskForUpdate));
        assertEquals(2, manager.getAllTasks().size(), "Задача не должна удалиться из менеджера");
        assertSame(task1, manager.getPrioritizedTasks().getFirst());
    }
}
