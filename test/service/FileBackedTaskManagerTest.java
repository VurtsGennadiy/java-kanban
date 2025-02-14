package service;

import model.*;
import exceptions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    Path path;

    FileBackedTaskManagerTest() throws IOException {
        path = Paths.get("test","fileBackedTaskManagerTest.csv");
    }

    @BeforeEach
    @Override
    void init() {
        manager = FileBackedTaskManager.loadFromFile(path);
        super.init();
    }

    @AfterEach
    void tearDown() {
        try {
            Files.delete(path);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    @Override
    void createNewTaskAndFindById() {
        super.createNewTaskAndFindById();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            Task recordTask = manager.fromString(reader.readLine());

            assertFalse(reader.ready(), "В файл записаны лишние строки");
            assertEquals(task, recordTask, "Task некорректно сохранился в файл");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void createNewSubtaskAndFindById() {
        super.createNewSubtaskAndFindById();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            Epic recordEpic = (Epic) manager.fromString(reader.readLine());
            Subtask recordSubtask = (Subtask) manager.fromString(reader.readLine());

            assertFalse(reader.ready(), "В файл записаны лишние строки");
            assertEquals(subtask, recordSubtask, "Subtask некорректно сохранился в файл");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void createNewEpicAndFindById() {
        super.createNewEpicAndFindById();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            Epic recordEpic = (Epic) manager.fromString(reader.readLine());

            assertFalse(reader.ready(), "В файл записаны лишние строки");
            assertEquals(epic, recordEpic, "Epic некорректно сохранился в файл");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void updateTask() {
        super.updateTask();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            Task recordTask = manager.fromString(reader.readLine());

            assertFalse(reader.ready(), "В файл записаны лишние строки");
            assertEquals(task.getId(), recordTask.getId(), "id задачи не должен измениться");
            assertNotEquals(task.getDescription(), recordTask.getDescription(),
                    "обновленный description не записался в файл");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void updateEpic() {
        super.updateEpic();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            Epic recordEpic = (Epic) manager.fromString(reader.readLine());

            assertFalse(reader.ready(), "В файл записаны лишние строки");
            assertEquals(epic.getId(), recordEpic.getId(), "id задачи не должен измениться");
            assertEquals(epic.getName(), recordEpic.getName(),
                    "обновленный name не записался в файл");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void updateSubtask() {
        super.updateSubtask();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            Epic recordEpic  = (Epic) manager.fromString(reader.readLine());
            Subtask recordSubtask = (Subtask) manager.fromString(reader.readLine());

            assertFalse(reader.ready(), "В файл записаны лишние строки");
            assertEquals(subtask.getId(), recordSubtask.getId(), "Subtask Id не должен измениться");
            assertEquals(epic.getId(), recordEpic.getId(), "Epic Id не должен измениться");
            assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Не записался новый статус");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void removeTaskById() {
        super.removeTaskById();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            assertFalse(reader.ready(), "Task не удалился из файла");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void removeEpicById() {
        super.removeEpicById();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            assertFalse(reader.ready(), "Epic и его subtask не удалились из файла");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void removeSubtaskById() {
        super.removeSubtaskById();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            Epic recordEpic = (Epic) manager.fromString(reader.readLine());
            assertEquals(epic, recordEpic, "Epic должен остаться в файле");
            assertFalse(reader.ready(), "В файле должен остались лишние строки epic");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void clearAllTasks() {
        super.clearAllTasks();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            assertFalse(reader.ready(), "Task не удалился из файла");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void clearAllEpics() {
        super.clearAllEpics();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            assertFalse(reader.ready(), "Epic и его subtask не удалились из файла");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Override
    void clearAllSubtasks() {
        super.clearAllSubtasks();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine(); // skip title
            Epic recordEpic = (Epic) manager.fromString(reader.readLine());
            assertEquals(epic, recordEpic, "Epic должен остаться в файле");
            assertFalse(reader.ready(), "В файле должен остались лишние строки epic");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
        String expectedStringFromTask = "0,TASK,Task1_Name,NEW,Task1_Description,1735718400,3600";
        String expectedStringFromSubtask = "0,SUBTASK,Subtask1_Name,NEW,Subtask1_Of_Epic1,1735812000,1800,-1";
        String expectedStringFromEpic = "0,EPIC,Epic1_Name,NEW,Epic_Of_One_Subtask";

        assertEquals(expectedStringFromTask, manager.toString(task));
        assertEquals(expectedStringFromSubtask, manager.toString(subtask));
        assertEquals(expectedStringFromEpic, manager.toString(epic));
    }

    @Test
    void taskFromString() {
        LocalDateTime expectedStartTime = LocalDateTime.of(2025,1,1,10,0);
        Duration expectedDuration = Duration.ofMinutes(45);
        String taskStr = "0,TASK,Task1_Name,NEW,Task1_Description,"
                + expectedStartTime.toEpochSecond(ZoneOffset.UTC) + "," + expectedDuration.toSeconds();
        Task task = manager.fromString(taskStr);

        assertEquals(0, task.getId(), "Некорректно установлено поле id");
        assertEquals(TaskType.TASK, task.getType(), "Некорректный тип задачи");
        assertEquals("Task1_Name", task.getName(), "Некорректно установлено поле name");
        assertEquals("Task1_Description", task.getDescription(),
                "Некорректно установлено поле description");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Некорректно установлено поле status");
        assertEquals(expectedStartTime, task.getStartTime(), "Некорректно установлено поле startTime");
        assertEquals(expectedDuration, task.getDuration(), "Некорректно установлено поле duration");
    }

    @Test
    void taskFromStringWithNullFields() {
        String taskStr = "0,TASK,null,NEW,null,null,null";
        Task task = manager.fromString(taskStr);

        assertEquals(0, task.getId(), "Некорректно установлено поле id");
        assertEquals(TaskType.TASK, task.getType(), "Некорректный тип задачи");
        assertNull(task.getName(), "Некорректно установлено поле name");
        assertNull(task.getDescription(),
                "Некорректно установлено поле description");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Некорректно установлено поле status");
        assertNull(task.getStartTime(), "Некорректно установлено поле startTime");
        assertNull(task.getDuration(), "Некорректно установлено поле duration");
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
        assertTrue(epic.getSubtasksId().isEmpty());

    }

    @Test
    void epicFromStringWithNullFields() {
        String epicStr = "1,EPIC,null,IN_PROGRESS,null";
        Epic epic = (Epic) manager.fromString(epicStr);

        assertEquals(1, epic.getId());
        assertEquals(TaskType.EPIC, epic.getType());
        assertNull(epic.getName());
        assertNull(epic.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        assertTrue(epic.getSubtasksId().isEmpty());
    }

    @Test
    void subtaskFromString() {
        LocalDateTime expectedStartTime = LocalDateTime.of(2025, 1, 2, 8, 0);
        Duration expectedDuration = Duration.ofHours(3);
        String subtaskStr = "3,SUBTASK,Subtask1_Name,DONE,Subtask1_Description,"
                + expectedStartTime.toEpochSecond(ZoneOffset.UTC) + "," + expectedDuration.getSeconds() + ",0";
        Subtask subtask = (Subtask) manager.fromString(subtaskStr);

        assertEquals(3, subtask.getId());
        assertEquals(TaskType.SUBTASK, subtask.getType());
        assertEquals("Subtask1_Name", subtask.getName());
        assertEquals("Subtask1_Description", subtask.getDescription());
        assertEquals(TaskStatus.DONE, subtask.getStatus());
        assertEquals(expectedStartTime, subtask.getStartTime(), "Некорректно установлено поле startTime");
        assertEquals(expectedDuration, subtask.getDuration(), "Некорректно установлено поле duration");
    }

    @Test
    void subtaskFromStringWithNullFields() {
        String subtaskStr = "3,SUBTASK,null,DONE,null,null,null,0";
        Subtask subtask = (Subtask) manager.fromString(subtaskStr);

        assertEquals(3, subtask.getId());
        assertEquals(TaskType.SUBTASK, subtask.getType());
        assertNull(subtask.getName());
        assertNull(subtask.getDescription());
        assertEquals(TaskStatus.DONE, subtask.getStatus());
        assertNull(subtask.getStartTime(), "Некорректно установлено поле startTime");
        assertNull(subtask.getDuration(), "Некорректно установлено поле duration");
        assertEquals(0, subtask.getEpicId());
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
        String expectedTitleString = "id,type,name,status,description,startTime,duration,epic";
        manager.createNewTask(task);
        String expectedTaskString = manager.toString(task);

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
        writer.write("id,type,name,status,description,startTime,duration,epic");
        writer.newLine();
        task.setId(0);
        epic.setId(1);
        subtask.setId(2);
        subtask.setEpicId(1);
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
}
