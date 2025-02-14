package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

public class HistoryManagerTest {
    static HistoryManager historyManager;
    static List<Task> history;
    static Task task;
    static Subtask subtask;
    static Epic epic;

    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Task1_Name","Task1_Description");
        epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask");
        subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1", 2);
        task.setId(1);
        epic.setId(2);
        subtask.setId(3);
    }

    @Test
    void shouldReturnEmptyHistoryWhenNoAdds() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldAddInHistoryAllTaskTypes() {
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
    void shouldNoChangeTaskInHistory() {
        TaskManager manager = Managers.getDefault();
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
        historyManager.addTask(null);
        assertTrue(historyManager.getHistory().isEmpty(), "Null добавился в историю");
    }

    @Test
    void shouldReturnCopyOfHistoryList() {
        historyManager.addTask(task);
        List<Task> history = historyManager.getHistory();
        assertNotSame(history, historyManager.getHistory(),
                "HistoryManager должен отдавать новый список");
    }


    @Test
    void shouldLeftLastViewWhenAddContainsTask() {
        historyManager.addTask(task);
        historyManager.addTask(subtask);
        historyManager.addTask(task);
        history = historyManager.getHistory();

        assertEquals(2, history.size(),
                "Две задачи с равными id не должны добавиться в историю");
        assertSame(task, history.getLast(), "Должен остаться последний просмотр");
    }

    @Test
    void removeTaskWhenOneInHistory() {
        historyManager.addTask(task);
        historyManager.remove(task.getId());
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty(), "Единственная задача не удалилась из истории");
    }

    @Test
    void removeFirstTaskInHistory() {
        Task task1 = task;
        Task task2 = new Task();
        Task task3 = new Task();
        task2.setId(2);
        task3.setId(3);

        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "Размер истории должен уменьшиться");
        assertFalse(history.contains(task1), "Задача не удалилась из истории");
    }

    @Test
    void removeTaskFromMiddleInHistory() {
        Task task1 = task;
        Task task2 = new Task();
        Task task3 = new Task();
        task2.setId(2);
        task3.setId(3);

        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.remove(2);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "Задача не удалилась из середины истории");
    }

    @Test
    void removeLastTaskInHistory() {
        Task task1 = task;
        Task task2 = new Task();
        Task task3 = new Task();
        task2.setId(2);
        task3.setId(3);

        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.remove(3);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "Размер истории должен уменьшиться");
        assertFalse(history.contains(task3), "Задача не удалилась из истории");
    }
}
