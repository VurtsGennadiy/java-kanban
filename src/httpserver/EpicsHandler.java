package httpserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler {
    EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processGet(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (Pattern.matches("^/epics/\\d+.*$", path)) {
            String pathId = path.split("/")[2];
            int id = Integer.parseInt(pathId);
            if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                List<Subtask> subtasks = taskManager.getSubtasksOfEpic(id);
                sendText(exchange, gson.toJson(subtasks));
            } else if (Pattern.matches("^/epics/\\d+$", path)) {
                Epic epic = taskManager.getEpic(id);
                sendText(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange);
            }
        } else if (Pattern.matches("^/epics$", path)) {
            List<Epic> epics = taskManager.getAllEpics();
            String json = gson.toJson(epics);
            sendText(exchange, json);
        } else {
            sendNotFound(exchange);
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (!Pattern.matches("^/epics$", path)) {
            sendNotFound(exchange);
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        JsonObject inputJson = JsonParser.parseString(body).getAsJsonObject();
        JsonElement inputId = inputJson.get("id");
        Epic epic = gson.fromJson(inputJson, Epic.class); // 5
        if (inputId == null || inputId.getAsInt() == 0) {
            taskManager.createNewEpic(epic);
            System.out.println("Создан новый эпик: " + epic);
        } else {
            taskManager.updateEpic(epic);
            System.out.println("Обновлен эпик: " + epic);
        }
        sendEmpty(exchange, 201);
    }

    @Override
    protected void processDelete(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (!Pattern.matches("^/epics/\\d+$", path)) {
            sendNotFound(exchange);
            return;
        }
        String pathId = path.split("/")[2];
        int id = Integer.parseInt(pathId);
        taskManager.removeEpic(id);
        sendEmpty(exchange, 200);
        System.out.println("Удалён эпик id = " + id);
    }

    @Override
    public String getAllowedMethods() {
        return "GET, POST, DELETE";
    }
}
