package httpserver;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.SortedSet;
import java.util.regex.Pattern;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    public PrioritizedTasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String patch = exchange.getRequestURI().getPath();
        if (method.equals("GET") && Pattern.matches("^/prioritized$", patch)) {
            SortedSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            sendText(exchange, gson.toJson(prioritizedTasks));
        } else {
            sendNotFound(exchange);
        }
    }
}
