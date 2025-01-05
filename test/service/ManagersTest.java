package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    void shouldReturnReadyHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertInstanceOf(InMemoryHistoryManager.class, historyManager,
                "Класс отличается от InMemoryHistoryManager");

        historyManager.addTask(new Task());
        assertEquals(1, historyManager.getHistory().size(),
                "Менеджер истории не добавляет задачи");
    }

    @Test
    void shouldReturnReadyTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertInstanceOf(InMemoryTaskManager.class, taskManager,
                "Класс отличается от InMemoryTaskManager");

        taskManager.createNewTask(new Task());
        assertEquals(1, taskManager.getAllTasks().size(),
                "Менеджер задач не добавляет задачи");
    }

    @Test
    void getFileBackedTaskManager() throws IOException {
        Path path = Path.of("ManagersTest.csv");
        TaskManager taskManager = Managers.getFileBackedTaskManager(path);

        assertInstanceOf(FileBackedTaskManager.class, taskManager,
                "Класс отличается от FileBackedTaskManager");

        taskManager.createNewTask(new Task());
        assertEquals(1, taskManager.getAllTasks().size(),
                "Менеджер задач не добавляет задачи");

        Files.delete(path);
    }
}
