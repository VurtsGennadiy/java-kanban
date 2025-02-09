package httpserver;

import com.google.gson.reflect.TypeToken;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest extends BaseHttpHandlerTest {
    public HistoryHandlerTest() throws IOException {
        super("/history");
    }

    @Test
    void getEmptyHistory() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> actual = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertTrue(actual.isEmpty());
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        manager.getTask(task.getId());
        manager.getTask(task.getId());
        manager.getSubtask(subtask.getId());
        manager.getEpic(epic.getId());

        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> actual = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(3, actual.size());
        assertEquals(task, actual.getFirst());
        assertEquals(subtask, actual.get(1));
        assertEquals(epic, actual.getLast());
    }
}
