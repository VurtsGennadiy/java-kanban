package httpserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processGet(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (Pattern.matches("^/subtasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/subtasks/", "");
            int id = Integer.parseInt(pathId);
            Subtask subtask = taskManager.getSubtask(id);
            sendText(exchange, gson.toJson(subtask));
        } else if (Pattern.matches("^/subtasks$", path)) {
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            sendText(exchange, gson.toJson(subtasks));
        } else {
            sendNotFound(exchange);
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (!Pattern.matches("^/subtasks$", path)) {
            sendNotFound(exchange);
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        JsonObject inputJson = JsonParser.parseString(body).getAsJsonObject();
        JsonElement inputId = inputJson.get("id");
        Subtask subtask = gson.fromJson(inputJson, Subtask.class);
        if (inputId == null || inputId.getAsInt() == 0) {
            taskManager.createNewSubtask(subtask);
            System.out.println("Создана новая подзадача: " + subtask);
        } else {
            taskManager.updateSubtask(subtask);
            System.out.println("Обновлена подзадача: " + subtask);
        }
        sendEmpty(exchange, 201);
    }

    @Override
    protected void processDelete(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (!Pattern.matches("^/subtasks/\\d+$", path)) {
            sendNotFound(exchange);
            return;
        }
        String pathId = path.replaceFirst("/subtasks/", "");
        int id = Integer.parseInt(pathId);
        taskManager.removeSubtask(id);
        System.out.println("Удалена подзадача id = " + id);
        sendEmpty(exchange, 200);
    }

    @Override
    public String getAllowedMethods() {
        return "GET, POST, DELETE";
    }
}
