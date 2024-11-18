package service;

import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();

    static Task task;
    static Subtask subtask;
    static Epic epic;

    @BeforeAll
    static void init() {
        task = new Task("Task1_Name","Task1_Description");
        epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask");
        subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1", epic);
    }

    @Test
    void canAddAllTaskTypes() {
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
}
