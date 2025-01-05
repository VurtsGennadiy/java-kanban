package service;

import java.nio.file.Path;

public abstract class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(Path path) {
        return FileBackedTaskManager.loadFromFile(path);
    }
}
