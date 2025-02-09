package httpserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerSaveException;
import exceptions.TaskHasIntersectException;
import exceptions.TaskNotFoundException;
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
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        /*
            get/ getAllTask 200
            get/id getTaskById 200 404
            post/ body id+ createTask 201 406
            post/ body id- updateTask 201 406
            delete/id deleteTask 200 404
         */
        try {
            if (Pattern.matches("^/tasks/\\d+$", path)) {
                String pathId = path.replaceFirst("/tasks/", "");
                int id = Integer.parseInt(pathId);

                switch (method) {
                    case "GET" -> handleGetTaskById(id, exchange);
                    case "DELETE" -> handleDeleteTask(id, exchange);
                    default -> sendNotFound(exchange);
                }
            } else if (Pattern.matches("^/tasks$", path)) {
                switch (method) {
                    case "GET" -> handleGetAllTasks(exchange);
                    case "POST" -> handlePostTask(exchange);
                    default -> sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (TaskNotFoundException exception) {
            sendNotFound(exchange);
        } catch (TaskHasIntersectException exception) {
            sendNotAcceptable(exchange, exception.getMessage());
        } catch (JsonSyntaxException | IllegalStateException exception) {
            sendNotAcceptable(exchange, "Некорректное тело запроса");
        } catch (ManagerSaveException exception) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGetTaskById(int id, HttpExchange exchange) {
        Task task = taskManager.getTask(id);
        sendText(exchange, gson.toJson(task));
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
        if (inputId == null || inputId.getAsInt() == 0) {
            taskManager.createNewTask(task);
            System.out.println("Создана новая задача: " +  task);
        } else {
            taskManager.updateTask(task);
            System.out.println("Обновлена задача: " + task);
        }
        sendEmpty(exchange, 201);
    }

    private void handleDeleteTask(int id, HttpExchange exchange) {
        taskManager.removeTask(id);
        sendEmpty(exchange, 200);
        System.out.println("Удалена задача id = " + id);
    }
}
