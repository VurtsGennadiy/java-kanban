package httpserver;

import com.google.gson.reflect.TypeToken;
import model.Task;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("Некорректный адрес или метод запроса")
    void invalidRequest() throws IOException, InterruptedException {
        URI uri1 = URI.create(BASE_URL + "/abc");
        URI uri2 = URI.create(BASE_URL);
        HttpRequest request1 = HttpRequest.newBuilder(uri1).GET().build();
        HttpRequest request2 = HttpRequest.newBuilder(uri2).POST(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response1.statusCode());
        assertEquals(405, response2.statusCode());
    }
}
