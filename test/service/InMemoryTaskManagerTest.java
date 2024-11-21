package service;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class InMemoryTaskManagerTest {
    static Task task;
    static Subtask subtask;
    static Epic epic;

    @BeforeAll
    static void init() {
        task = new Task("Task1_Name","Task1_Description");
        epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask");
        subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1", epic);
    }

    @AfterEach
    void resetTasks() {
        init();
    }

    @Test
    void canAddAllTaskTypesAndFindById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
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
    void canClearAllTaskTypes() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.createNewTask(task);
        manager.createNewSubtask(subtask);
        manager.createNewEpic(epic);

        manager.clearAllTasks();
        manager.clearAllSubtasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Не очистился список Task");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Не очистился список Subtask");
        assertFalse(manager.getAllEpics().isEmpty(), "Epic не должен удалиться вместе с Subtask");

        manager.createNewSubtask(subtask);
        manager.clearAllEpics();
        assertTrue(manager.getAllEpics().isEmpty(), "Не очистился список Epic");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Subtask не существует отдельно от эпика");
    }

    @Test
    void canReturnSubtasksOfEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Subtask subtask = manager.createNewSubtask(InMemoryTaskManagerTest.subtask);
        Epic epic = manager.createNewEpic(InMemoryTaskManagerTest.epic);
        List<Subtask> subtasks = manager.getSubtasksOfEpic(epic.getId());
        assertSame(subtask, subtasks.getFirst(), "Subtask не добавился в Epic");
    }

    @Test
    void canRemoveTaskById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = manager.createNewTask(InMemoryTaskManagerTest.task);
        Subtask subtask = manager.createNewSubtask(InMemoryTaskManagerTest.subtask);
        Epic epic = manager.createNewEpic(InMemoryTaskManagerTest.epic);

        manager.removeTask(5);
        assertEquals(1, manager.getAllTasks().size(), "Не должно ничего удалиться");
        assertEquals(1, manager.getAllSubtasks().size(), "Не должно ничего удалиться");
        assertEquals(1, manager.getAllEpics().size(), "Не должно ничего удалиться");

        manager.removeTask(task.getId());
        manager.removeTask(subtask.getId());
        assertNull(manager.getTask(task.getId()), "Не удалился Task");
        assertNull(manager.getSubtask(subtask.getId()), "Не удалился Subtask");

        manager.createNewSubtask(subtask);
        manager.removeTask(epic.getId());

        assertNull(manager.getEpic(epic.getId()), "Не удалился Epic");
        assertNull(manager.getSubtask(subtask.getId()), "Subtask не существует отдельно от эпика");
    }

    @Test
    void canUpdateTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = manager.createNewTask(InMemoryTaskManagerTest.task);
        Task updTask = new Task();
        updTask.setId(task.getId());
        manager.updateTask(updTask);

        assertEquals(1, manager.getAllTasks().size(), "Не должен добавиться Task");
        assertNotSame(task, manager.getTask(task.getId()), "Task не обновился");
    }

    @Test
    void canUpdateEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = manager.createNewEpic(InMemoryTaskManagerTest.epic);
        Epic updEpic = new Epic();
        updEpic.setId(epic.getId());
        manager.updateTask(updEpic);

        assertEquals(1, manager.getAllEpics().size(), "Не должен добавиться Epic");
        assertNotSame(epic, manager.getEpic(epic.getId()), "Epic не обновился");
    }

    @Test
    void canUpdateSubtask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
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
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task addedTask = new Task(task.getName(), task.getDescription());
        addedTask.setStatus(task.getStatus());
        addedTask = manager.createNewTask(addedTask);

        assertEquals(task.getName(), addedTask.getName(), "Изменилось поле Name");
        assertEquals(task.getDescription(), addedTask.getDescription(), "Изменилось поле Description");
        assertEquals(task.getStatus(), addedTask.getStatus(), "Изменилось поле Status");
    }

    @Test
    void shouldNoChangeSubtaskFieldsAfterAdd() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
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
        InMemoryTaskManager manager = new InMemoryTaskManager();
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
        InMemoryTaskManager manager = new InMemoryTaskManager();
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
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.createNewEpic(epic);
        Subtask subtask2 = new Subtask("subtask2_name", "subtask2_description", epic);
        manager.createNewSubtask(subtask);
        manager.createNewSubtask(subtask2);

        manager.checkEpicStatus(epic.getId());
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус должен быть NEW");

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.checkEpicStatus(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS");

        subtask.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        manager.checkEpicStatus(epic.getId());
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус должен быть DONE");
    }
}
