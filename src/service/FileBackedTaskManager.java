package service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import exceptions.*;
import model.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    private FileBackedTaskManager(Path path) throws IOException {
        this.path = path;
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString(), StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,startTime,duration,epic");
            bw.newLine();
            for (Task task : getAllTasks()) {
                bw.write(toString(task));
                bw.newLine();
            }
            for (Epic epic : getAllEpics()) {
                bw.write(toString(epic));
                bw.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                bw.write(toString(subtask));
                bw.newLine();
            }
        } catch (IOException ex) {
            throw new ManagerSaveException("Не удалось выполнить сохранение задач в файл", ex);
        }
    }

    public String toString(Task task) {
        char separator = ',';
        char separatorToReplace = '.';
        StringJoiner joiner = new StringJoiner(String.valueOf(separator));
        joiner.add(String.valueOf(task.getId()));
        joiner.add(String.valueOf(task.getType()));
        joiner.add(task.getName() != null ?
                task.getName().replace(separator, separatorToReplace) : "null");
        joiner.add(String.valueOf(task.getStatus()));
        joiner.add(task.getDescription() != null ?
                task.getDescription().replace(separator,separatorToReplace) : "null");
        if (task.getType() != TaskType.EPIC) {
            joiner.add(task.getStartTime() != null ?
                    String.valueOf(task.getStartTime().toEpochSecond(ZoneOffset.UTC)) : "null");
            joiner.add(task.getDuration() != null ?
                    String.valueOf(task.getDuration().get(ChronoUnit.SECONDS)) : "null");
            if (task.getType() == TaskType.SUBTASK) {
                joiner.add(String.valueOf(((Subtask) task).getEpicId()));
            }
        }
        return joiner.toString();
    }

    public Task fromString(String value) {
        try {
            String[] fields = value.split(",");
            Task task = null;
            int id = Integer.parseInt(fields[0]);
            TaskType taskType = TaskType.valueOf(fields[1]);
            String name = !fields[2].equals("null") ? fields[2] : null;
            TaskStatus status = TaskStatus.valueOf(fields[3]);
            String description = !fields[4].equals("null") ? fields[4] : null;
            LocalDateTime startTime = null;
            Duration duration = null;
            if (fields.length > 5) {
                startTime = !fields[5].equals("null") ?
                        LocalDateTime.ofEpochSecond(Integer.parseInt(fields[5]),0, ZoneOffset.UTC) : null;
                duration = !fields[6].equals("null") ?
                        Duration.ofSeconds(Integer.parseInt(fields[6])) : null;
            }
            switch (taskType) {
                case TASK -> task = new Task(name, description, startTime, duration);
                case SUBTASK -> {
                    int epicId = Integer.parseInt(fields[7]);
                    //Epic epicOfSubtask = this.getEpic(epicId);
                    task = new Subtask(name, description, startTime, duration, epicId);
                }
                case EPIC -> task = new Epic(name, description);
            }
            task.setStatus(status);
            task.setId(id);
            return task;
        } catch (Exception ex) {
            throw new ManagerParseTaskException("Неверный формат строки: " + value, ex);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager;
        try {
            manager = new FileBackedTaskManager(path);
        } catch (IOException ex) {
            throw new ManagerLoadFileException("Ошибка создания файла " + path.getFileName(), ex);
        }
        try  (BufferedReader reader = new BufferedReader(new FileReader(path.toString(), StandardCharsets.UTF_8))) {
            reader.readLine(); // skip title string
            int taskIdMaxValue = -1;
            while (reader.ready()) {
                String line = reader.readLine();
                Task task = manager.fromString(line);
                manager.idCounter = task.getId();
                if (manager.idCounter > taskIdMaxValue) {
                    taskIdMaxValue = manager.idCounter;
                }
                switch (task.getType()) {
                    case TASK -> manager.createNewTask(task);
                    case SUBTASK -> manager.createNewSubtask((Subtask) task);
                    case EPIC -> manager.createNewEpic((Epic) task);
                }
            }
            manager.idCounter = taskIdMaxValue + 1;
            return manager;
        } catch (IOException | ManagerParseTaskException ex) {
            throw new ManagerLoadFileException("Ошибка чтения файла " + path.getFileName(), ex);
        }
    }

    @Override
    public Task createNewTask(Task task) {
        super.createNewTask(task);
        save();
        return task;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void removeTask(Integer id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(Integer id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(Integer id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    public static void main(String[] args) {
        Path path = Paths.get("res","manager.csv");
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(path);
        Task task = new Task("Task1_Name","Task1_Description");
        manager1.createNewTask(task);
        Epic epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask");
        manager1.createNewEpic(epic);
        Subtask subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1", epic.getId());
        manager1.createNewSubtask(subtask);

        List<Task> tasksManager1 = new ArrayList<>(manager1.getAllTasks());
        tasksManager1.addAll(manager1.getAllEpics());
        tasksManager1.addAll(manager1.getAllSubtasks());
        for (Task item : tasksManager1) {
            System.out.println(item);
        }

        // создаём другой объект менеджера из этого же файла
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(path);
        System.out.println();
        List<Task> tasksManager2 = new ArrayList<>(manager2.getAllTasks());
        tasksManager2.addAll(manager2.getAllEpics());
        tasksManager2.addAll(manager2.getAllSubtasks());
        for (Task item : tasksManager2) {
            System.out.println(item);
        }

        try {
            Files.delete(path);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
