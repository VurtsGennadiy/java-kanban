package httpserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processGet(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (Pattern.matches("^/tasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/tasks/", "");
            int id = Integer.parseInt(pathId);
            Task task = taskManager.getTask(id);
            sendText(exchange, gson.toJson(task));
        } else if (Pattern.matches("^/tasks$", path)) {
            List<Task> tasks = taskManager.getAllTasks();
            sendText(exchange, gson.toJson(tasks));
        } else {
            sendNotFound(exchange); // +
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (!Pattern.matches("^/tasks$", path)) {
            sendNotFound(exchange);
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        JsonObject inputJson = JsonParser.parseString(body).getAsJsonObject();
        JsonElement inputId = inputJson.get("id");
        Task task = gson.fromJson(inputJson, Task.class);
        if (inputId == null || inputId.getAsInt() == 0) {
            taskManager.createNewTask(task);
            System.out.println("Создана новая задача: " +  task);
        } else {
            taskManager.updateTask(task);
            System.out.println("Обновлена задача: " + task);
        }
        sendEmpty(exchange, 201);
    }

    @Override
    protected void processDelete(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (!Pattern.matches("^/tasks/\\d+$", path)) {
            sendNotFound(exchange);
            return;
        }
        String pathId = path.replaceFirst("/tasks/", "");
        int id = Integer.parseInt(pathId);
        taskManager.removeTask(id);
        sendEmpty(exchange, 200);
        System.out.println("Удалена задача id = " + id);
    }

    @Override
    public String getAllowedMethods() {
        return "GET, POST, DELETE";
    }
}
