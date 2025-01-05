package service;

import model.*;
import exceptions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager manager;
    Task task;
    Subtask subtask;
    Epic epic;
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
    void readEmptyFile() throws IOException {
        Path emptyFilePath = Files.createFile(Paths.get("EmptyTestFile.csv"));
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(emptyFilePath);

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());

        Files.delete(emptyFilePath);
    }

    @Test
    void createManagerWithNotExistFile() throws IOException {
        Path notExistFilePath = Paths.get("NotExistFile.csv");
        assertTrue(Files.notExists(notExistFilePath), "Файл не должен существовать на диске");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(notExistFilePath);

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());

        Files.delete(notExistFilePath);
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

    @Test
    void epicFromString() {
        String epicStr = "1,EPIC,Epic1_Name,IN_PROGRESS,Epic_Without_Subtasks";
        Epic epic = (Epic) manager.fromString(epicStr);

        assertEquals(1, epic.getId());
        assertEquals(TaskType.EPIC, epic.getType());
        assertEquals("Epic1_Name", epic.getName());
        assertEquals("Epic_Without_Subtasks", epic.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void subtaskFromString() {
        String subtaskStr = "2,SUBTASK,Subtask1_Name,DONE,Subtask1_Description,0";
        Subtask subtask = (Subtask) manager.fromString(subtaskStr);

        assertEquals(2, subtask.getId());
        assertEquals(TaskType.SUBTASK, subtask.getType());
        assertEquals("Subtask1_Name", subtask.getName());
        assertEquals("Subtask1_Description", subtask.getDescription());
        assertEquals(TaskStatus.DONE, subtask.getStatus());
    }

    @Test
    void fromString_invalidString_throwManagerParseTaskException() {
        String invalidStr = "invalid string";
        assertThrows(ManagerParseTaskException.class, () -> {
            manager.fromString(invalidStr);
        });
    }

    @Test
    void saveInFile() throws IOException {
        String expectedTitleString = "id,type,name,status,description,epic";
        String expectedTaskString = manager.toString(task);
        manager.createNewTask(task);

        assertTrue(Files.exists(path), "Файл не сохранился на диске");
        BufferedReader reader = new BufferedReader(new FileReader(path.toString()));
        assertEquals(expectedTitleString, reader.readLine());
        assertEquals(expectedTaskString, reader.readLine());
        assertFalse(reader.ready(), "В файле есть лишние строки");

        reader.close();
    }

    @Test
    void loadFromFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()));
        writer.write("id,type,name,status,description,epic");
        writer.newLine();
        task.setId(0);
        epic.setId(1);
        subtask.setId(2);
        writer.write(manager.toString(task));
        writer.newLine();
        writer.write(manager.toString(epic));
        writer.newLine();
        writer.write(manager.toString(subtask));
        writer.newLine();
        writer.close();

        manager = FileBackedTaskManager.loadFromFile(path);
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtasks().size());
        assertEquals(task, manager.getAllTasks().getFirst());
        assertEquals(epic, manager.getAllEpics().getFirst());
        assertEquals(subtask, manager.getAllSubtasks().getFirst());
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
