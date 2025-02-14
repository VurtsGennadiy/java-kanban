package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerCreateTaskException;
import exceptions.ManagerSaveException;
import exceptions.TaskHasIntersectException;
import exceptions.TaskNotFoundException;
import gsonadapters.DurationAdapter;
import gsonadapters.LocalDateTimeAdapter;
import gsonadapters.TaskDeserializer;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

abstract class BaseHttpHandler implements HttpHandler {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;
    protected Gson gson;

    BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET" -> processGet(exchange);
                case "POST" -> processPost(exchange);
                case "DELETE" -> processDelete(exchange);
                default -> sendNotAllowed(exchange);
            }
        } catch (TaskNotFoundException exception) {
            sendNotFound(exchange);
        } catch (TaskHasIntersectException | ManagerCreateTaskException exception) {
            sendNotAcceptable(exchange, exception.getMessage());
        } catch (JsonSyntaxException | IllegalStateException exception) {
            sendNotAcceptable(exchange, "Некорректное тело запроса");
        } catch (ManagerSaveException exception) {
            sendInternalServerError(exchange);
        }
    }

    protected void processGet(HttpExchange exchange) {
        sendNotAllowed(exchange);
    }

    protected void processPost(HttpExchange exchange) throws IOException {
        sendNotAllowed(exchange);
    }

    protected void processDelete(HttpExchange exchange) {
        sendNotAllowed(exchange);
    }

    public abstract String getAllowedMethods();

    public void sendNotAllowed(HttpExchange exchange) {
        try (exchange) {
            exchange.getResponseHeaders().add("Allow", getAllowedMethods());
            exchange.sendResponseHeaders(405, 0);
        } catch (IOException exception) {
            System.out.println("Ошибка отправки ответа от сервера");
        }
    }

    public void sendText(HttpExchange exchange, String text) {
        try (exchange; OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, 0);
            os.write(text.getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Ошибка отправки ответа от сервера");
        }
    }

    public void sendEmpty(HttpExchange exchange, int code)   {
        try (exchange) {
            exchange.sendResponseHeaders(code, 0);
        } catch (IOException exception) {
            System.out.println("Ошибка отправки ответа от сервера");
        }
    }

    protected void sendNotFound(HttpExchange exchange) {
        try (exchange; OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(404, 0);
            os.write("Страница не найдена".getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Ошибка отправки ответа от сервера");
        }
    }

    public void sendNotAcceptable(HttpExchange exchange, String text) {
        try (exchange; OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(406, 0);
            os.write(text.getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Ошибка отправки ответа от сервера");
        }
    }

    public void sendInternalServerError(HttpExchange exchange) {
        try (exchange; OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(500, 0);
            os.write("Ошибка сервера при обработке запроса".getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            System.out.println("Ошибка отправки ответа от сервера");
        }
    }
}
