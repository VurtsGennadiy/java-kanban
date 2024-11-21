package service;

import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManagerTest {
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
    void shouldReturnEmptyHistoryWhenNoAdds() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldAddInHistoryAllTaskTypes() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        List<Task> expected = new ArrayList<>();
        expected.add(task);
        expected.add(subtask);
        expected.add(epic);
        historyManager.addTask(task);
        historyManager.addTask(subtask);
        historyManager.addTask(epic);
        assertEquals(expected, historyManager.getHistory());
    }

    @Test
    void shouldNoOverFlowHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        for (int i = 0; i < InMemoryHistoryManager.HISTORY_LIST_SIZE; i++) {
            historyManager.addTask(subtask);
        }
        assertEquals(InMemoryHistoryManager.HISTORY_LIST_SIZE,
                historyManager.getHistory().size(),
                "История не заполнилась полностью");

        List<Task> expected = new ArrayList<>();
        for (int i = 0; i < InMemoryHistoryManager.HISTORY_LIST_SIZE; i++) {
            expected.add(task);
            historyManager.addTask(task);
        }

        assertFalse(historyManager.getHistory().size()
                        > InMemoryHistoryManager.HISTORY_LIST_SIZE,
                "История наполнилась больше ограничения");
        assertEquals(expected, historyManager.getHistory(), "Старые записи не удаляются");
    }

    @Test
    void shouldNoChangeTaskInHistory() {
        TaskManager manager = new InMemoryTaskManager();
        Task createdTask = manager.createNewTask(task);
        Task saved = manager.getTask(createdTask.getId()); // get task, add in history
        String name = saved.getName();
        int id = createdTask.getId();

        // update task in TaskManager
        Task taskForUpdate = new Task("upd_task_name", "upd_task_description");
        taskForUpdate.setId(id);
        manager.updateTask(taskForUpdate);

        Task actual = manager.getHistory().getFirst();
        assertNotEquals(name, manager.getTask(id).getName(), "Не обновился task в TaskManager");
        assertEquals(name, actual.getName(), "Задача в истории изменилась");
    }

    @Test
    void shouldNoAddNull() {
        TaskManager manager = new InMemoryTaskManager();
        Task task = manager.getTask(1);
        assertNull(task, "TaskManager не вернул null для добавления в историю");
        assertEquals(0, manager.getHistory().size(), "Null добавился в историю");
    }

    @Test
    void shouldReturnCopyOfHistoryList() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.addTask(task);
        List<Task> history = historyManager.getHistory();
        assertNotSame(history, historyManager.getHistory(),
                "HistoryManager должен отдавать новый список");
    }
}
