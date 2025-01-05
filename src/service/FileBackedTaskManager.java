package service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import exceptions.*;
import model.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    private FileBackedTaskManager(Path path) throws IOException{
        this.path = path;
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString(), StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,epic");
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
        String epicId = "";
        if (task instanceof Subtask subtask) {
            epicId = String.format(",%d", subtask.getEpic().getId());
        }
        return String.format("%d,%s,%s,%s,%s%s",
                task.getId(),
                task.getType(),
                task.getName() != null ? task.getName().replace(',','.') : "null",
                task.getStatus(),
                task.getDescription() != null ? task.getDescription().replace(',','.') : "null",
                epicId);
    }

    public Task fromString(String value) {
        try {
            String[] fields = value.split(",");
            Task task;
            switch (fields[1]) {
                case "TASK" -> task = new Task();
                case "SUBTASK" -> task = new Subtask();
                case "EPIC" -> task = new Epic();
                default -> throw new IllegalArgumentException("Неверный формат TaskType");
            }
            switch (fields[3]) {
                case "NEW" -> task.setStatus(TaskStatus.NEW);
                case "IN_PROGRESS" -> task.setStatus(TaskStatus.IN_PROGRESS);
                case "DONE" -> task.setStatus(TaskStatus.DONE);
                default -> throw new IllegalArgumentException("Неверный формат TaskStatus");
            }
            task.setId(Integer.parseInt(fields[0]));
            String taskName = !fields[2].equals("null") ? fields[2] :  null;
            String taskDescription = !fields[4].equals("null") ? fields[4] : null;
            task.setName(taskName);
            task.setDescription(taskDescription);
            // !!! Эпики должны быть инициализированы раньше, чем субтаски
            if (task instanceof Subtask subtask) {
                Integer epicId = Integer.parseInt(fields[5]);
                subtask.setEpic(this.getEpic(epicId));
            }
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
        try  (BufferedReader reader = new BufferedReader(new FileReader(path.toString(), StandardCharsets.UTF_8))){
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
    public void removeTask(Integer id) {
        super.removeTask(id);
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
        Path path = Paths.get("manager.csv");
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(path);
        manager1.createNewTask(new Task());
        Task task = new Task("Task1_Name","Task1_Description");
        Epic epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask");
        Subtask subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1", epic);

        manager1.createNewTask(task);
        manager1.createNewSubtask(subtask);
        manager1.createNewEpic(epic);

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
