package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import exceptions.*;
import model.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(Path path) {
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

    }

    public String toString(Task task) {
        String epicId = "";
        if (task instanceof Subtask subtask) {
            epicId = String.format(",%d", subtask.getEpic().getId());
        }
        return String.format("%d,%s,%s,%s,%s%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId);
    }
}
