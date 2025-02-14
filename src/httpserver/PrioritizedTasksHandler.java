package httpserver;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.util.SortedSet;
import java.util.regex.Pattern;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    public PrioritizedTasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processGet(HttpExchange exchange) {
        String patch = exchange.getRequestURI().getPath();
        if (!Pattern.matches("^/prioritized$", patch)) {
            sendNotFound(exchange);
            return;
        }
        SortedSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        sendText(exchange, gson.toJson(prioritizedTasks));
    }

    @Override
    public String getAllowedMethods() {
        return "GET";
    }
}
