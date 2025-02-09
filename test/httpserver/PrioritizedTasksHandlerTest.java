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

public class PrioritizedTasksHandlerTest extends BaseHttpHandlerTest{
    public PrioritizedTasksHandlerTest() throws IOException {
        super("/prioritized");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> actual = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(2, actual.size());
        assertEquals(subtask, actual.getFirst());
        assertEquals(task, actual.getLast());
    }
}