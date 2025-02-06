package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import gsonadapters.DurationAdapter;
import gsonadapters.LocalDateTimeAdapter;
import gsonadapters.SubtaskAdapter;
import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/epics", new EpicsHandler(manager));
        server.createContext("/subtasks", new SubtasksHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(manager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP сервер запущен по адресу: " + server.getAddress());
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP сервер остановлен.");
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        Task task = manager.createNewTask(new Task("Task1_name", "Task1_description",
                LocalDateTime.of(2025, 1, 2, 10, 0), Duration.ofHours(1)));
        Epic epic = new Epic("epic1_name", "epic1_description");
        manager.createNewEpic(epic);
        Subtask subtask = manager.createNewSubtask(new Subtask("subtask1_name", "subtask1_description",
                LocalDateTime.of(2025,1,1,10,0), null, epic));

        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}
