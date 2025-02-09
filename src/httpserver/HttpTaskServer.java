package httpserver;

import com.sun.net.httpserver.HttpServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/epics", new EpicsHandler(manager));
        server.createContext("/subtasks", new SubtasksHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(manager));
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public void start() {
        server.start();
        System.out.println("HTTP сервер запущен по адресу: " + server.getAddress());
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP сервер остановлен.");
    }

    public InetSocketAddress getAddress() {
        return server.getAddress();
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getFileBackedTaskManager(Paths.get("res","tasks.csv"));
        try {
            HttpTaskServer server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.out.println("Ошибка создания веб сервера. " + e.getMessage());
        }
    }
}
