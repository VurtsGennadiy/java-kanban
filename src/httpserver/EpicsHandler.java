package httpserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerCreateTaskException;
import exceptions.ManagerSaveException;
import exceptions.TaskNotFoundException;
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
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        /* + get/ - getAllEpics - 200
           + get/id - getEpic(id) - 200 404
           + get/id/subtasks - getSubtasksOfEpic(id) - 200 404
           + POST/ - createEpic 201
           + POST/id - update 201 404
           + DELETE/id - deleteEpic 200 404
         */
        try {
            if (Pattern.matches("^/epics/\\d+.*$", path)) {
                String pathId = path.split("/")[2];
                int id = Integer.parseInt(pathId);

                if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                    handleGetEpicSubtasks(id, exchange);
                } else if (Pattern.matches("^/epics/\\d+$", path)) {
                    switch (method) {
                        case "GET" -> handleGetEpicById(id, exchange);
                        case "DELETE" -> handleDeleteEpicById(id, exchange);
                        default -> sendNotFound(exchange);
                    }
                } else {
                    sendNotFound(exchange);
                }
            } else if (Pattern.matches("^/epics$", path)) {
                switch (method) {
                    case "GET" -> handleGetAllEpics(exchange);
                    case "POST" -> handlePostEpic(exchange);
                    default -> sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (TaskNotFoundException exception) {
            sendNotFound(exchange);
        } catch (JsonSyntaxException | IllegalStateException exception) {
            sendNotAcceptable(exchange, "Некорректное тело запроса");
        } catch (ManagerCreateTaskException exception) {
            sendNotAcceptable(exchange, exception.getMessage());
        } catch (ManagerSaveException exception) {
            sendInternalServerError(exchange);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        JsonObject inputJson = JsonParser.parseString(body).getAsJsonObject();
        JsonElement inputId = inputJson.get("id");
        Epic epic = gson.fromJson(inputJson, Epic.class);
        if (inputId == null || inputId.getAsInt() == 0) {
            taskManager.createNewEpic(epic);
            System.out.println("Создан новый эпик: " + epic);
        } else {
            taskManager.updateEpic(epic);
            System.out.println("Обновлен эпик: " + epic);
        }
        sendEmpty(exchange, 201);
    }

    private void handleDeleteEpicById(int id, HttpExchange exchange) {
        taskManager.removeEpic(id);
        sendEmpty(exchange, 200);
        System.out.println("Удалён эпик id = " + id);
    }

    private void handleGetEpicSubtasks(int epicId, HttpExchange exchange) {
        List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epicId);
        sendText(exchange, gson.toJson(subtasks));
    }

    private void handleGetEpicById(int id, HttpExchange exchange) {
        Epic epic = taskManager.getEpic(id);
        sendText(exchange, gson.toJson(epic));
    }

    private void handleGetAllEpics(HttpExchange exchange) {
        List<Epic> epics = taskManager.getAllEpics();
        String json = gson.toJson(epics);
        sendText(exchange, json);
    }
}
