package service;

import model.*;
import java.util.List;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    static InMemoryTaskManager manager;
    static Task task;
    static Subtask subtask;
    static Epic epic;

    @BeforeEach
    void init() {
        manager = new InMemoryTaskManager();
        task = new Task("Task1_Name","Task1_Description");
        epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask");
        subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1", epic);
    }

    @Test
    void addAllTaskTypesAndFindById() {
        manager.createNewTask(task);
        manager.createNewSubtask(subtask);
        manager.createNewEpic(epic);

        assertEquals(1, manager.getAllTasks().size(), "Не добавился task");
        assertEquals(1, manager.getAllSubtasks().size(), "Не добавился subtask");
        assertEquals(1, manager.getAllEpics().size(), "Не добавился epic");

        // check find by id
        Integer taskId  = manager.getAllTasks().getFirst().getId();
        Integer subtaskId  = manager.getAllSubtasks().getFirst().getId();
        Integer epicId  = manager.getAllEpics().getFirst().getId();

        assertSame(task, manager.getTask(taskId), "Не находит task по id");
        assertSame(subtask, manager.getSubtask(subtaskId), "Не находит subtask по id");
        assertSame(epic, manager.getEpic(epicId), "Не находит epic по id");
    }

    @Test
    void clearAllTasks() {
        manager.createNewTask(task);
        manager.getTask(task.getId());
        manager.clearAllTasks();

        assertTrue(manager.getAllTasks().isEmpty(), "Не очистился список tasks в менеджере");
        assertFalse(manager.getHistory().contains(task), "Task не удалился из истории");
    }

    @Test
    void clearAllSubtasks() {
        manager.createNewSubtask(subtask);
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
    }

    @Test
    void clearAllEpics() {
        manager.createNewSubtask(subtask);
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
    }

    @Test
    void getSubtasksOfEpic() {
        Subtask subtask = manager.createNewSubtask(InMemoryTaskManagerTest.subtask);
        Epic epic = manager.createNewEpic(InMemoryTaskManagerTest.epic);
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
    }

    @Test
    void removeSubtaskById() {
        manager.createNewSubtask(subtask);
        manager.createNewEpic(epic);
        int subtaskId = subtask.getId();

        manager.getSubtask(subtaskId);
        manager.removeTask(subtaskId);

        assertNull(manager.getSubtask(subtaskId), "subtask не удалился из taskManager");
        assertTrue(epic.getSubtasks().isEmpty(), "subtask не удалился из epic");
        assertEquals(Collections.emptyList(), manager.getHistory(), "subtask не удалился из historyManager");
    }

    @Test
    void removeEpicById() {
        manager.createNewEpic(epic);
        manager.createNewSubtask(subtask);

        manager.getSubtask(subtask.getId());
        manager.getEpic(epic.getId());
        manager.removeTask(epic.getId());

        assertNull(manager.getEpic(epic.getId()), "Не удалился epic");
        assertNull(manager.getSubtask(subtask.getId()), "Не удалился subtask связанный с epic");
        assertEquals(Collections.emptyList(), manager.getHistory(), "Не очистилась история");
    }

    @Test
    void updateTask() {
        Task task = manager.createNewTask(InMemoryTaskManagerTest.task);
        Task updTask = new Task();
        updTask.setId(task.getId());
        manager.updateTask(updTask);

        assertEquals(1, manager.getAllTasks().size(), "Должен остаться 1 task");
        assertNotSame(task, manager.getTask(task.getId()), "Task не заменился в менеджере");
    }

    @Test
    void updateEpic() {
        Epic epic = manager.createNewEpic(InMemoryTaskManagerTest.epic);
        Epic updEpic = new Epic();
        updEpic.setId(epic.getId());
        manager.updateTask(updEpic);

        assertEquals(1, manager.getAllEpics().size(), "Должен остаться 1 epic");
        assertNotSame(epic, manager.getEpic(epic.getId()), "Epic не заменился в менеджере");
    }

    @Test
    void updateSubtask() {
        Epic epic = manager.createNewEpic(InMemoryTaskManagerTest.epic);
        Subtask subtask = manager.createNewSubtask(InMemoryTaskManagerTest.subtask);

        Subtask updSubtask = new Subtask();
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
    }

    @Test
    void shouldNoChangeTaskFieldsAfterAdd() {
        Task addedTask = new Task(task.getName(), task.getDescription());
        addedTask.setStatus(task.getStatus());
        addedTask = manager.createNewTask(addedTask);

        assertEquals(task.getName(), addedTask.getName(), "Изменилось поле Name");
        assertEquals(task.getDescription(), addedTask.getDescription(), "Изменилось поле Description");
        assertEquals(task.getStatus(), addedTask.getStatus(), "Изменилось поле Status");
    }

    @Test
    void shouldNoChangeSubtaskFieldsAfterAdd() {
        Subtask addedSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getEpic());
        addedSubtask.setStatus(subtask.getStatus());
        addedSubtask = manager.createNewSubtask(addedSubtask);

        assertEquals(subtask.getName(), addedSubtask.getName(), "Изменилось поле Name");
        assertEquals(subtask.getDescription(), addedSubtask.getDescription(), "Изменилось поле Description");
        assertEquals(subtask.getStatus(), addedSubtask.getStatus(), "Изменилось поле Status");
        assertSame(subtask.getEpic(), addedSubtask.getEpic(), "Изменилось поле Epic");
    }

    @Test
    void shouldNoChangeEpicFieldsAfterAdd() {
        Epic addedEpic = new Epic(epic.getName(), epic.getDescription(), epic.getSubtasks());
        addedEpic.setStatus(epic.getStatus());
        addedEpic = manager.createNewEpic(addedEpic);

        assertEquals(epic.getName(), addedEpic.getName(), "Изменилось поле Name");
        assertEquals(epic.getDescription(), addedEpic.getDescription(), "Изменилось поле Description");
        assertEquals(epic.getStatus(), addedEpic.getStatus(), "Изменилось поле Status");
        assertSame(epic.getSubtasks(), addedEpic.getSubtasks(), "Изменилось поле Subtasks");
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
    void shouldSetEpicStatusBasedOnSubtasksStatus() {
        manager.createNewEpic(epic);
        epic.setStatus(TaskStatus.DONE);
        Subtask subtask2 = new Subtask("subtask2_name", "subtask2_description", epic);
        manager.createNewSubtask(subtask);
        manager.createNewSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус должен быть NEW");

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS");

        subtask.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateTask(subtask);
        manager.updateTask(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус должен быть DONE");
    }
}
