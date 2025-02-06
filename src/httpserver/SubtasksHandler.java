package httpserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskHasIntersectException;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class SubtasksHandler extends BaseHttpHandler{
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        /* + get/ getAllSubtasks 200;
           + get/id getSubtaskById 200 404
           + post/ body id- createSubtask 201 406;
           + post/ body id+ updateSubtask 201 406
           + delete/id deleteSubtask 200
         */
        if (Pattern.matches("^/subtasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/subtasks/", "");
            int id = Integer.parseInt(pathId);

            switch (method) {
                case "GET" -> handleGetSubtaskById(id, exchange);
                case "DELETE" -> handleDeleteSubtaskById(id, exchange);
                default -> sendNotFound(exchange);
            }
        } else if (Pattern.matches("^/subtasks+$", path)) {
            switch (method) {
                case "GET" -> handleGetAllSubtasks(exchange);
                case "POST" -> handlePostSubtasks(exchange);
                default -> sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostSubtasks(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        JsonObject inputJson = JsonParser.parseString(body).getAsJsonObject();
        JsonElement inputId = inputJson.get("id");
        Subtask subtask = gson.fromJson(inputJson, Subtask.class);
        if (inputId == null) {
            try {
                taskManager.createNewSubtask(subtask);
            } catch (TaskHasIntersectException exception) {
                sendHasIntersections(exchange);
                return;
            }
        } else {
            taskManager.updateTask(subtask);
        }
        sendEmpty(exchange, 201);
    }

    private void handleGetAllSubtasks(HttpExchange exchange) {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendText(exchange, gson.toJson(subtasks));
    }

    private void handleDeleteSubtaskById(int id, HttpExchange exchange) {
        taskManager.removeTask(id);
        sendEmpty(exchange, 200);
    }

    private void handleGetSubtaskById(int id, HttpExchange exchange) {
        Subtask subtask = taskManager.getSubtask(id);
        if (subtask == null) {
            sendNotFound(exchange);
        } else {
            sendText(exchange, gson.toJson(subtask));
        }
    }
}
