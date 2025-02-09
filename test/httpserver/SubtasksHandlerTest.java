package httpserver;

import com.google.gson.reflect.TypeToken;
import model.Subtask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtasksHandlerTest extends BaseHttpHandlerTest {
    public SubtasksHandlerTest() throws IOException {
        super("/subtasks");
    }

    @Test
    @DisplayName("Создать подзадачу")
    public void createSubtask() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2025, 2, 8, 21, 0);
        Duration duration = Duration.ofMinutes(5);
        String name = "new_subtask_name";
        String description = "new_subtask_description";
        Subtask subtask = new Subtask(name, description, startTime, duration, epic.getId());
        String subtaskJson = gson.toJson(subtask);

        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Subtask> subtasks = manager.getAllSubtasks();
        Subtask actual = subtasks.getLast();
        assertEquals(2, subtasks.size(), "Подзадача не добавилась в менеджер");
        assertTrue(epic.getSubtasksId().contains(actual.getId()), "Подзадача не добавилась в эпик");

        assertEquals(name, actual.getName());
        assertEquals(description, actual.getDescription());
        assertEquals(duration, actual.getDuration());
        assertTrue(startTime.isEqual(actual.getStartTime()));
    }

    @Test
    @DisplayName("Создать подзадачу, пересечение")
    void createTaskWithIntersection() throws IOException, InterruptedException {
        Subtask subtask2 = new Subtask("subtask_with_intersection", "",
                subtask.getStartTime(), null, epic.getId());

        String subtask2Json = gson.toJson(subtask2);
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(406, response.statusCode());
        assertEquals(1, subtasks.size(), "Задача не должна добавиться в менеджер");
    }

    @Test
    @DisplayName("Получить все подзадачи")
    void getAllSubtasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Type listSubtaskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        List<Subtask> actual =  gson.fromJson(response.body(), listSubtaskType);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(subtask, actual.getFirst());
    }

    @Test
    @DisplayName("Получить подзадачу по id")
    void getSubtaskById() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask actual = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(actual);
        assertEquals(subtask.getId(), actual.getId());
        assertEquals(subtask.getName(), actual.getName());
        assertEquals(subtask.getDescription(), actual.getDescription());
        assertEquals(subtask.getEpicId(), actual.getEpicId());
        assertEquals(subtask.getStartTime(), actual.getStartTime());
        assertEquals(subtask.getDuration(), actual.getDuration());
        assertEquals(subtask.getStatus(), actual.getStatus());
    }

    @Test
    @DisplayName("Получить подзадачу по id not found")
    void getSubtaskByIdNotFound() throws IOException, InterruptedException {
        int id = 0;
        URI uri = URI.create(BASE_URL + "/" + id);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Удалить подзадачу по id")
    void deleteSubtaskById() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertFalse(manager.getAllSubtasks().contains(subtask));
    }

    @Test
    @DisplayName("Удалить подзадачу по id not found")
    void deleteSubtaskByIdNotFound() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/100000");
        HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Обновить подзадачу")
    void updateSubtask() throws IOException, InterruptedException {
        String name = "upd_name";
        Subtask subtaskForUpdate = new Subtask(name, "", null, null, subtask.getEpicId());
        subtaskForUpdate.setId(subtask.getId());
        String subtaskJson = gson.toJson(subtaskForUpdate);

        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = manager.getAllSubtasks();
        Subtask actual = manager.getSubtask(subtask.getId());

        assertEquals(201, response.statusCode());
        assertEquals(1, subtasks.size(), "В менеджере должна остаться 1 задача");
        assertEquals(name, actual.getName());
    }

    @Test
    @DisplayName("Обновить подзадачу, пересечение")
    void updateSubtaskWithIntersection() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2025,2,8, 11,0);
        manager.createNewSubtask(new Subtask("subtask_with_intersection", "",
                startTime, Duration.ofMinutes(10), subtask.getEpicId()));
        Subtask subtaskForUpdate = new Subtask("upd_name", "", startTime, null, subtask.getEpicId());
        subtaskForUpdate.setId(subtask.getId());

        String subtaskJson = gson.toJson(subtaskForUpdate);
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    @DisplayName("Обновить подзадачу, not found")
    void updateSubtaskNotFound() throws IOException, InterruptedException {
        String name = "upd_name";
        Subtask subtaskForUpdate = new Subtask(name, "", null, null, subtask.getEpicId());
        subtaskForUpdate.setId(100000);
        String subtaskJson = gson.toJson(subtaskForUpdate);

        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(404, response.statusCode());
        assertEquals(1, subtasks.size(), "В менеджере должна остаться 1 задача");
        assertFalse(subtasks.contains(subtaskForUpdate));
    }
}
