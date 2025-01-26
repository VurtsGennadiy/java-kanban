package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void shouldBeNotEqualsWhenNotEqualIds() {
        Task task1 = new Task("Task_name", "Task_description");
        Task task2 = new Task("Task_name", "Task_description");
        task1.setId(1);
        task2.setId(2);
        assertNotEquals(task1, task2, "Таски равны");
        assertNotEquals(task2, task1, "Таски равны");
    }

    @Test
    void shouldBeEqualsWhenEqualIds() {
        Task task1 = new Task("Task1_name", "Task1_description");
        Task task2 = new Task("Task2_name", "Task2_description");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Таски не равны");
        assertEquals(task2, task1, "Таски не равны");
    }

    @Test
    void shouldBeEqualsItself() {
        Task task1 = new Task("Task1_name", "Task1_description");
        task1.setId(1);
        assertEquals(task1, task1, "Таска не равна сама себе");
    }

    @Test
    void throwExceptionWhenTaskDurationIsNegativeOrZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("Name", "Description", LocalDateTime.now(), Duration.ofHours(-1));
        }, "Продолжительность задачи не может быть отрицательная");
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("Name", "Description", LocalDateTime.now(), Duration.ofSeconds(0));
        }, "Продолжительность задачи не может быть 0 секунд");
    }

    @Test
    void getEndTime() {
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        Duration duration = Duration.ofHours(1);
        LocalDateTime endTime = startTime.plus(duration);

        Task nullDurationTask = new Task("Task 1", "Task with null duration");
        Task haveDurationTask = new Task("Task 2", "Task with startTime and duration",
                startTime, duration);

        assertNull(nullDurationTask.getEndTime());
        assertEquals(endTime, haveDurationTask.getEndTime());
    }
}
