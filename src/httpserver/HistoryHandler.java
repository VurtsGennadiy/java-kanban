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
    protected void processGet(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (!Pattern.matches("^/history$", path)) {
            sendNotFound(exchange);
            return;
        }
        List<Task> history = taskManager.getHistory();
        sendText(exchange, gson.toJson(history));
    }

    @Override
    public String getAllowedMethods() {
        return "GET";
    }
}
