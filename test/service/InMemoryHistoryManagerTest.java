package service;

import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManagerTest {
    static InMemoryHistoryManager historyManager;
    static Task task;
    static Subtask subtask;
    static Epic epic;

    @BeforeAll
    static void init() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Task1_Name","Task1_Descr");
        subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1");
        epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask", new ArrayList<>(List.of(subtask)));
    }

    @BeforeEach
    void clearHistory() {
        historyManager.getHistory().clear();
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
    void shouldNoOverFlowHistory() {
        List<Task> expected = new ArrayList<>();
        historyManager.addTask(subtask);
        assertEquals(1, historyManager.getHistory().size());

        for (int i = 0; i < InMemoryHistoryManager.HISTORY_LIST_SIZE; i++) {
            expected.add(task);
            historyManager.addTask(task);
        }
        assertEquals(expected, historyManager.getHistory());
    }
}
