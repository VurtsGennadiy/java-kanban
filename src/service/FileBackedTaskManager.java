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

    private FileBackedTaskManager(Path path) {
        this.path = path;
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException ex) {
                throw (new ManagerCreateFileException(path, ex));
            }
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString(), StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                bw.write(toString(task));
            }
            for (Epic epic : getAllEpics()) {
                bw.write(toString(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                bw.write(toString(subtask));
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
        return String.format("%d,%s,%s,%s,%s%s\n",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId);
    }

    public Task fromString(String value) {
        Task task = null;
        try {
            String[] fields = value.split(",");
            switch (fields[1]) {
                case "TASK" -> task = new Task();
                case "SUBTASK" -> task = new Subtask();
                case "EPIC" -> task = new Epic();
            }
            switch (fields[3]) {
                case "NEW" -> task.setStatus(TaskStatus.NEW);
                case "IN_PROGRESS" -> task.setStatus(TaskStatus.IN_PROGRESS);
                case "DONE" -> task.setStatus(TaskStatus.DONE);
            }
            task.setId(Integer.parseInt(fields[0]));
            task.setName(fields[2]);
            task.setDescription(fields[4]);
            // !!!!
            if (task instanceof Subtask subtask) {
                Integer epicId = Integer.parseInt(fields[5]);
                subtask.setEpic(this.getEpic(epicId));
            }
        } catch (Exception ex) {
            throw new ManagerParseTaskException("Неверный формат строки: " + value, ex);
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString(), StandardCharsets.UTF_8))) {
            reader.readLine(); // skip title string
            int taskIdMaxValue = 0;
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return manager;
    }

    public static void main(String[] args) {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(Paths.get("Tasks.csv"));
/*      Task task = new Task("Task1_Name","Task1_Description");
        Epic epic = new Epic("Epic1_Name", "Epic_Of_One_Subtask");
        Subtask subtask = new Subtask("Subtask1_Name", "Subtask1_Of_Epic1", epic);

        manager.createNewTask(task);
        manager.createNewSubtask(subtask);
        manager.createNewEpic(epic);
        manager.save();*/
        List<Task> tasks = new ArrayList<>(manager.getAllTasks());
        tasks.addAll(manager.getAllEpics());
        tasks.addAll(manager.getAllSubtasks());

        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
