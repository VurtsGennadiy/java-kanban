package httpserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskHasIntersectException;
import exceptions.TaskNotFoundException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Optional<Integer> taskId = getTaskId(exchange);
        // id - есть, нет, некорректный
        /* get : id+ getTask, id- getAllTask 200
        post: id+ createTask, id- updateTask 201
        delete: id+ - deleteTask, id- 404? 200
        id -1 404
         */
        if (taskId.isPresent()) {
            int id = taskId.get();
            if (id == -1) { // invalid id
                sendNotFound(exchange);
                return;
            }
            switch (method) {
                case "GET" -> handleGetTaskById(id, exchange);
                case "DELETE" -> handleDeleteTask(id, exchange);
                default -> sendNotFound(exchange);
            };
        } else {
            switch (method) {
                case "GET" -> handleGetAllTasks(exchange);
                case "POST" -> handlePostTask(exchange);
                default -> sendNotFound(exchange);
            };
        }

    }

    private void handleGetTaskById(int id, HttpExchange exchange) {
        try {
            Task task = taskManager.getTask(id);
            sendText(exchange, gson.toJson(task));
        } catch (TaskNotFoundException exception) {
            sendNotFound(exchange);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) {
        List<Task> tasks = taskManager.getAllTasks();
        sendText(exchange, gson.toJson(tasks));
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        JsonObject inputJson = JsonParser.parseString(body).getAsJsonObject();
        JsonElement inputId = inputJson.get("id");
        Task task = gson.fromJson(inputJson, Task.class);
        try {
            if (inputId == null) {
                taskManager.createNewTask(task);
            } else {
                taskManager.updateTask(task);
            }
            sendEmpty(exchange, 201);
        } catch (TaskHasIntersectException exception) {
            sendNotAcceptable(exchange, exception.getMessage());
        } catch (TaskNotFoundException exception) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteTask(int id, HttpExchange exchange) {
        try {
            taskManager.removeTask(id);
            sendEmpty(exchange, 200);
        } catch (TaskNotFoundException exception) {
            sendNotFound(exchange);
        }
    }
}
