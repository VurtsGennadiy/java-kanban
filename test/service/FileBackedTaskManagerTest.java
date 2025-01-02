package service;

import model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    static FileBackedTaskManager manager;
    static Task task;
    static Subtask subtask;
    static Epic epic;
    Path path;

    @BeforeEach
    void init() {
        path = Paths.get("fileBackedTaskManagerTest.csv");
        manager = FileBackedTaskManager.loadFromFile(path);
        task = new Task("Task1_Name","Task1_Description");
        epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask");
        subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1", epic);
    }

    @Test
    void taskToString() {
        String expectedStringFromTask = "0,TASK,Task1_Name,NEW,Task1_Description";
        String expectedStringFromSubtask = "0,SUBTASK,Subtask1_Name,NEW,Subtask1_Of_Epic1,0";
        String expectedStringFromEpic = "0,EPIC,Epic1_Name,NEW,Epic_Of_One_Subtask";

        assertEquals(expectedStringFromTask, manager.toString(task));
        assertEquals(expectedStringFromSubtask, manager.toString(subtask));
        assertEquals(expectedStringFromEpic, manager.toString(epic));
    }

    @Test
    void taskFromString() {
        String taskStr = "0,TASK,Task1_Name,NEW,Task1_Description";
        Task task = manager.fromString(taskStr);

        assertEquals(0, task.getId());
        assertEquals(TaskType.TASK, task.getType());
        assertEquals("Task1_Name", task.getName());
        assertEquals("Task1_Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }


    @AfterEach
    void tearDown() {
        try {
            Files.delete(path);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
