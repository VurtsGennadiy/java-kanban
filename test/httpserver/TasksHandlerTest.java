package httpserver;

import com.google.gson.reflect.TypeToken;
import model.*;
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

public class TasksHandlerTest extends BaseHttpHandlerTest {
    public TasksHandlerTest() throws IOException {
        super("/tasks");
    }

    @Test
    @DisplayName("Получить все задачи")
    void getAllTasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Type listTaskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actual =  gson.fromJson(response.body(), listTaskType);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(task, actual.getFirst());
    }

    @Test
    @DisplayName("Получить задачу по id")
    void getTaskById() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task actual = gson.fromJson(response.body(), Task.class);
        assertNotNull(actual);
        assertEquals(task.getId(), actual.getId());
        assertEquals(task.getName(), actual.getName());
        assertEquals(task.getDescription(), actual.getDescription());
        assertEquals(task.getStatus(), actual.getStatus());
        assertEquals(task.getStartTime(), actual.getStartTime());
        assertEquals(task.getDuration(),actual.getDuration());
    }

    @Test
    @DisplayName("Получить задачу по id, not found")
    void getTaskByIdNotFound() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/" + 0);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Создать задачу")
    void createTask() throws IOException, InterruptedException {
        String name = "new_task_name";
        String description = "new_task_description";
        LocalDateTime startTime = LocalDateTime.of(2025,2,8,15,20);
        Duration duration = Duration.ofMinutes(10);
        Task newTask = new Task(name, description, startTime, duration);

        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = manager.getAllTasks();
        Task actual = tasks.getLast();

        assertEquals(201, response.statusCode());
        assertEquals(2, tasks.size());
        assertEquals(name, actual.getName());
        assertEquals(description, actual.getDescription());
        assertEquals(startTime, actual.getStartTime());
        assertEquals(duration, actual.getDuration());
    }

    @Test
    @DisplayName("Создать задачу, пересечение")
    void createTaskWithIntersection() throws IOException, InterruptedException {
        Task newTask = new Task("name", "description", task.getStartTime(), null);
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(406, response.statusCode());
        assertEquals(1, tasks.size());
        assertNotEquals(newTask.getName(), tasks.getFirst().getName());
    }

    @Test
    @DisplayName("Обновить задачу")
    void updateTask() throws IOException, InterruptedException {
        String name = "upd_name";
        Task taskForUpdate = new Task(name, "", null, null);
        taskForUpdate.setId(task.getId());
        String taskJson = gson.toJson(taskForUpdate);

        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = manager.getAllTasks();
        Task actual = manager.getTask(task.getId());

        assertEquals(201, response.statusCode());
        assertEquals(1, tasks.size(), "В менеджере должна остаться 1 задача");
        assertEquals(name, actual.getName());
    }

    @Test
    @DisplayName("Обновить задачу, пересечение")
    void updateTaskWithIntersection() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2025,2,8, 15,50);
        manager.createNewTask(new Task("subtask_with_intersection", "",
                startTime, Duration.ofMinutes(10)));
        Task taskForUpdate = new Task("upd_name", "", startTime, null);
        taskForUpdate.setId(task.getId());

        String taskJson = gson.toJson(taskForUpdate);
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    @DisplayName("Обновить задачу, not found")
    void updateTaskNotFound() throws IOException, InterruptedException {
        Task taskForUpdate = new Task("upd_name", "", null, null);
        taskForUpdate.setId(100000);

        String taskJson = gson.toJson(taskForUpdate);
        URI uri = URI.create(BASE_URL);
        HttpRequest request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = manager.getAllTasks();

        assertEquals(404, response.statusCode());
        assertEquals(1, tasks.size());
        assertFalse(tasks.contains(taskForUpdate));
    }

    @Test
    @DisplayName("Удалить задачу по id")
    void deleteTaskById() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertFalse(manager.getAllTasks().contains(task));
    }

    @Test
    @DisplayName("Удалить задачу по id, not found")
    void deleteTaskByIdNotFound() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "/100000");
        HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

/*    @Test
    @Override
    void invalidRequest() throws IOException, InterruptedException {
        super.invalidRequest();
        URI uri = URI.create(BASE_URL);
        HttpRequest request5 = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response5.statusCode());
    }*/

    @Test
    @DisplayName("Некорректный адрес или метод запроса")
    void invalidRequest() throws IOException, InterruptedException {
        URI uri1 = URI.create(BASE_URL + "/abc");
        URI uri2 = URI.create(BASE_URL + "/1");
        URI uri3 = URI.create(BASE_URL);

        HttpRequest request1 = HttpRequest.newBuilder(uri1).GET().build();
        HttpRequest request2 = HttpRequest.newBuilder(uri2).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpRequest request3 = HttpRequest.newBuilder(uri3).DELETE().build();
        HttpRequest request4 = HttpRequest.newBuilder(uri3).PUT(HttpRequest.BodyPublishers.noBody()).build();
        HttpRequest request5 = HttpRequest.newBuilder(uri3).POST(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response1.statusCode());
        assertEquals(404, response2.statusCode());
        assertEquals(404, response3.statusCode());
        assertEquals(405, response4.statusCode());
        assertEquals(406, response5.statusCode());
    }
}
