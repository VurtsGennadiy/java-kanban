package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gsonadapters.DurationAdapter;
import gsonadapters.LocalDateTimeAdapter;
import gsonadapters.TaskDeserializer;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract public class BaseHttpHandlerTest {
    final String BASE_URL;
    Gson gson;
    HttpTaskServer server;
    HttpClient client;
    TaskManager manager;
    Task task;
    Epic epic;
    Subtask subtask;
    Path filePath;

    BaseHttpHandlerTest(String path) throws IOException {
        filePath = Paths.get("res","test_manager.csv");
        //manager = Managers.getFileBackedTaskManager(filePath);
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .create();
        BASE_URL = "http:/" + server.getAddress().toString() + path;
    }

    @BeforeEach
    public void setUp() {
        server.start();
        client = HttpClient.newHttpClient();
        task = manager.createNewTask(new Task("task_name", "task_description",
                LocalDateTime.of(2025, 2, 8, 14, 0),
                Duration.ofMinutes(30)));
        epic = manager.createNewEpic(new Epic("epic_name", "epic_description"));
        subtask = manager.createNewSubtask(new Subtask("subtask_name", "subtask_description",
                LocalDateTime.of(2025, 2, 7, 21, 0),
                Duration.ofMinutes(5),
                epic.getId()));
    }

    @AfterEach
    public void shutDown() throws IOException {
        server.stop();
        //Files.deleteIfExists(filePath);
    }

    @Test
    @DisplayName("Некорректный адрес или метод запроса")
    void invalidRequest() throws IOException, InterruptedException {
        URI uri1 = URI.create(BASE_URL + "/abc");
        URI uri2 = URI.create(BASE_URL + "/1");
        URI uri3 = URI.create(BASE_URL);

        HttpRequest request1 = HttpRequest.newBuilder(uri1).GET().build();
        HttpRequest request2 = HttpRequest.newBuilder(uri2).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpRequest request3 = HttpRequest.newBuilder(uri3).DELETE().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response1.statusCode());
        assertEquals(404, response2.statusCode());
        assertEquals(404, response3.statusCode());
    }
}

