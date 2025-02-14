package httpserver;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EpicsHandlerTest extends BaseHttpHandlerTest {
    EpicsHandlerTest() throws IOException {
        super("/epics");
    }

    @Test
    @DisplayName("Получить все эпики")
    void getAllEpics() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> actual = gson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());
        assertEquals(200, response.statusCode());
        assertEquals(1, actual.size());
        assertEquals(epic, actual.getFirst());
    }

    @Test
    @DisplayName("Получить эпик по id")
    void getEpicById() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic actual = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals(epic.getId(), actual.getId());
        assertEquals(epic.getName(), actual.getName());
        assertEquals(epic.getDescription(), actual.getDescription());
        assertEquals(epic.getSubtasksId(), actual.getSubtasksId());
        assertEquals(epic.getStatus(), actual.getStatus());
    }

    @Test
    @DisplayName("Получить эпик по id, not found")
    void getEpicByIdNotFound() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/0");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Получить подзадачи эпика")
    void getEpicSubtasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> actual = gson.fromJson(response.body(), new TypeToken<List<Subtask>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, actual.size());
        assertEquals(subtask, actual.getFirst());
    }

    @Test
    @DisplayName("Получить подзадачи эпика, not found")
    void getEpicSubtaskNotFound() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Создать эпик")
    void createEpic() throws IOException, InterruptedException {
        String name = "new_epic_name";
        String description = "new_epic_description";
        Epic newEpic = new Epic(name, description);
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newEpic))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic actual = manager.getAllEpics().getLast();

        assertEquals(201, response.statusCode());
        assertEquals(2, manager.getAllEpics().size());
        assertEquals(name, actual.getName());
        assertEquals(description, actual.getDescription());
    }

    @Test
    @DisplayName("Обновить эпик")
    void updateEpic() throws IOException, InterruptedException {
        String name = "upd_epic_name";
        Epic epicForUpdate = new Epic(name, "");
        epicForUpdate.setId(epic.getId());
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicForUpdate))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic actual = manager.getAllEpics().getLast();

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(name, actual.getName());
        assertEquals(epic.getSubtasksId(), actual.getSubtasksId());
    }

    @Test
    @DisplayName("Обновить эпик, not found")
    void updateEpicNotFound() throws IOException, InterruptedException {
        Epic epicForUpdate = new Epic("","");
        epicForUpdate.setId(100000);

        String epicJson = gson.toJson(epicForUpdate);
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = manager.getAllEpics();

        assertEquals(404, response.statusCode());
        assertEquals(1, epics.size());
        assertFalse(epics.contains(epicForUpdate));
    }

    @Test
    @DisplayName("Удалить эпик по id")
    void deleteEpic() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertFalse(manager.getAllEpics().contains(epic));
    }

    @Test
    @DisplayName("Удалить эпик по id, not found")
    void deleteEpicByIdNotFound() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/100000");
        HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
    
/*    @Test
    @Override
    void invalidRequest() throws IOException, InterruptedException {
        super.invalidRequest();
        URI uri4 = URI.create(BASE_URL);
        URI uri5 = URI.create(BASE_URL + "0/subtasks/");

        HttpRequest request4 = HttpRequest.newBuilder(uri4).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpRequest request5 = HttpRequest.newBuilder(uri5).GET().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response4.statusCode());
        assertEquals(404, response5.statusCode());
    }*/

    @Test
    @DisplayName("Некорректный адрес или метод запроса")
    void invalidRequest() throws IOException, InterruptedException {
        URI uri1 = URI.create(BASE_URL + "/abc");
        URI uri2 = URI.create(BASE_URL + "/1");
        URI uri3 = URI.create(BASE_URL);
        URI uri6 = URI.create(BASE_URL + "/0/subtasks/");

        HttpRequest request1 = HttpRequest.newBuilder(uri1).GET().build();
        HttpRequest request2 = HttpRequest.newBuilder(uri2).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpRequest request3 = HttpRequest.newBuilder(uri3).DELETE().build();
        HttpRequest request4 = HttpRequest.newBuilder(uri3).PUT(HttpRequest.BodyPublishers.noBody()).build();
        HttpRequest request5 = HttpRequest.newBuilder(uri3).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpRequest request6 = HttpRequest.newBuilder(uri6).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response1.statusCode());
        assertEquals(404, response2.statusCode());
        assertEquals(404, response3.statusCode());
        assertEquals(405, response4.statusCode());
        assertEquals(406, response5.statusCode());
        assertEquals(404, response6.statusCode());
    }
}
