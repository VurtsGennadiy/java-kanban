package service;

import model.*;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    static InMemoryTaskManager manager = new InMemoryTaskManager();

    @BeforeAll
    static void createTasks() {
        Task task1 = new Task("Task1_Name","Task1_Descr");
        Task task2 = new Task("Task2_Name","Task2_Descr");

        Subtask subtask1 = new Subtask("Subtask1_Name", "Subtask1_OfEpic1");
        Subtask subtask2 = new Subtask("Subtask2_Name", "Subtask2_OfEpic1");
        ArrayList<Subtask> subtasksOfEpic1 = new ArrayList<>();
        subtasksOfEpic1.add(subtask1);
        subtasksOfEpic1.add(subtask2);

        Epic epic1 = new Epic("Epic1_Name", "Epic_Of_Two_Subtasks", subtasksOfEpic1);
        Epic epic2 = new Epic("Epic2_Name", "Epic_Of_One_Subtasks");
        Subtask subtask3 = new Subtask("Subtask3_Name", "Subtask3_OfEpic2_3ParamsConstructor", epic2);

        manager.createNewTask(task1);
        manager.createNewTask(task2);
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);
        manager.createNewSubtask(subtask3);
        manager.createNewEpic(epic1);
        manager.createNewEpic(epic2);
    }

    @AfterEach
    void clearHistory() {
        manager.getHistory().clear();
    }

    @Test
    void shouldReturnEmptyHistoryWhenNoViews() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnHistoryForAllTaskTypes() {
        manager.getHistory().clear();
        List<Task> expected = new ArrayList<>();
        expected.add(manager.getTask(1));
        expected.add(manager.getSubtask(3));
        expected.add(manager.getEpic(5));

        List<Task> actual = manager.getHistory();
        assertEquals(expected, actual);
    }

    @Test
    void shouldNoOverflowHistory() {
        manager.getTask(0);
        List<Task> expected = new ArrayList<>();
        for (int i = 0; i < InMemoryTaskManager.HISTORY_LIST_SIZE; i++) {
            expected.add(manager.getTask(1));
        }
        List<Task> actual = manager.getHistory();
        assertEquals(expected, actual);
    }
}