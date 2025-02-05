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
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler{
    EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        /* + get/ - getAllEpics - 200
           + get/id - getEpic(id) - 200, 404 если нет +
           + get/id/subtasks - getSubtasksOfEpic(id) - 200, 404 если эпика нет
           + POST/ - createEpic 201
           + POST/id - update 201
           + DELETE/id - deleteEpic 200
         */
        String method = exchange.getRequestMethod();
        String[] uri = exchange.getRequestURI().getPath().split("/");
        Optional<Integer> epicIdOp = getTaskId(exchange);

        if (epicIdOp.isPresent()) {
            int id = epicIdOp.get();
            if (id == -1) { // invalid id
                sendNotFound(exchange);
                return;
            }
            switch (method) {
                case "GET" -> {
                    if (uri.length == 4) {
                        if (uri[3].equals("subtasks")) {
                            handleGetEpicSubtasks(id, exchange);
                        }
                        else {
                            sendNotFound(exchange);
                        }
                    } else {
                        handleGetEpicById(id, exchange);
                    }
                }
                case "DELETE" -> handleDeleteEpicById(id, exchange);
                case "POST" -> handlePostEpic(exchange);
                default -> sendNotFound(exchange);
            }
        } else {
            switch (method) {
                case "GET" -> handleGetAllEpics(exchange);
                case "POST" -> handlePostEpic(exchange);
                default -> sendNotFound(exchange);
            }
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        JsonObject inputJson = JsonParser.parseString(body).getAsJsonObject();
        JsonElement inputId = inputJson.get("id");
        Epic epic = gson.fromJson(inputJson, Epic.class);
        if (inputId == null) {
            taskManager.createNewEpic(epic);
        } else {
            taskManager.updateTask(epic);
        }
        sendEmpty(exchange, 201);
    }

    private void handleDeleteEpicById(int id, HttpExchange exchange) {
        taskManager.removeTask(id);
        sendEmpty(exchange, 200);
    }

    private void handleGetEpicSubtasks(int epicId, HttpExchange exchange) {
        Epic epic = taskManager.getEpic(epicId);
        if (epic == null) {
            sendNotFound(exchange);
        } else {
            List<Subtask> subtasks = epic.getSubtasks();
            sendText(exchange, gson.toJson(subtasks));
        }
    }

    private void handleGetEpicById(int id, HttpExchange exchange) {
        Epic epic = taskManager.getEpic(id);
        if (epic == null) {
            sendNotFound(exchange);
        } else {
            sendText(exchange, gson.toJson(epic));
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) {
        List<Epic> epics = taskManager.getAllEpics();
        String json = gson.toJson(epics);
        sendText(exchange, json);
    }
}
