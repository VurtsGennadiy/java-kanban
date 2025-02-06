package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gsonadapters.DurationAdapter;
import gsonadapters.LocalDateTimeAdapter;
import gsonadapters.SubtaskAdapter;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;
    protected Gson gson;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // ТЗ
    public void sendText(HttpExchange exchange, String text) {
        try (exchange; OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(200, 0);
            if (!text.isEmpty()) {
                os.write(text.getBytes(DEFAULT_CHARSET));
            }
        } catch (Exception e) {
            System.out.println("Ошибка отправки ответа от сервера");
        }
    }

    // ТЗ
    protected void sendNotFound(HttpExchange exchange) {
        try (exchange; OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(404, 0);
            os.write("Страница не найдена".getBytes(DEFAULT_CHARSET));
        } catch (Exception e) {
            System.out.println("Ошибка отправки ответа от сервера");
        }
    }

    // ТЗ
    // TODO
    public void sendHasIntersections(HttpExchange exchange) {
        try (exchange; OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(406, 0);
            os.write("Невозможно добавить задачу, есть пересечение по времени.".getBytes(DEFAULT_CHARSET));
        } catch (Exception e) {
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

    BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {}

    protected Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            String pathId = pathParts[2];
            return Optional.of(Integer.parseInt(pathId));
        } catch (IndexOutOfBoundsException exception) { // id не передан в запросе
            return Optional.empty();
        } catch (NumberFormatException exception) { // некорректный id
            return Optional.of(-1);
        }
    }
}
