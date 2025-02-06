package httpserver;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.util.List;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {
    HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        if (method.equals("GET") && Pattern.matches("^/history$", path)) {
            List<Task> history = taskManager.getHistory();
            sendText(exchange, gson.toJson(history));
        } else {
            sendNotFound(exchange);
        }
    }
}
