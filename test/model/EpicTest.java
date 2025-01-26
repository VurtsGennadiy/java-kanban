package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTest {
    @Test
    void shouldBeNotEqualsWhenNotEqualIds() {
        Subtask subtask = new Subtask("Subtask_name", "Subtask_description");
        ArrayList<Subtask> subtasks = new ArrayList<>(List.of(subtask));
        Epic epic1 = new Epic("Epic_name", "Epic_description", subtasks);
        Epic epic2 = new Epic("Epic_name", "Epic_description", subtasks);
        epic1.setId(1);
        epic2.setId(2);

        assertNotEquals(epic1, epic2, "Эпики равны");
        assertNotEquals(epic2, epic1, "Эпики равны");
    }

    @Test
    void shouldBeEqualsWhenEqualIds() {
        Epic epic1 = new Epic("Epic1_name", "Epic1_description");
        Epic epic2 = new Epic("Epic2_name", "Epic2_description");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2, "Эпики не равны");
        assertEquals(epic2, epic1, "Эпики не равны");
    }

    @Test
    void shouldBeEqualsItself() {
        Epic epic = new Epic("Epic_name", "Epic_description");
        epic.setId(1);
        assertEquals(epic, epic, "Эпик не равен сам себе");
    }

    @Test
    void setTimeWithConstructor() {
        Duration startTaskDuration = Duration.ofHours(1);
        Duration middleTaskDuration = Duration.ofHours(2);
        Duration endTaskDuration = Duration.ofHours(3);
        Duration expectedDuration = startTaskDuration.plus(middleTaskDuration).plus(endTaskDuration);
        LocalDateTime startTime = LocalDateTime.of(2025,1,1,0,0);
        LocalDateTime endTaskStartTime = LocalDateTime.of(2025,1,3,0,0);
        LocalDateTime expectedEndTime = endTaskStartTime.plus(endTaskDuration);
        ArrayList<Subtask> subtasks = new ArrayList<>(List.of(
                new Subtask("end task","", endTaskStartTime, endTaskDuration),
                new Subtask("middle task", "",
                        LocalDateTime.of(2025,1,2,0,0),middleTaskDuration),
                new Subtask("start task", "", startTime, startTaskDuration)
        ));
        Epic epic = new Epic("Epic", "", subtasks);

        assertEquals(startTime, epic.getStartTime(), "Не верно установился startTime");
        assertEquals(expectedEndTime, epic.getEndTime(), "Не верно установился endTime");
        assertEquals(expectedDuration, epic.getDuration(), "Не верно установился duration");
    }

    @Test
    void setTimeWithAddSubtask() {
        Duration startTaskDuration = Duration.ofHours(1);
        Duration middleTaskDuration = Duration.ofHours(2);
        Duration endTaskDuration = Duration.ofHours(3);
        Duration expectedDuration = startTaskDuration.plus(middleTaskDuration).plus(endTaskDuration);
        LocalDateTime startTime = LocalDateTime.of(2025,1,1,0,0);
        LocalDateTime endTaskStartTime = LocalDateTime.of(2025,1,3,0,0);
        LocalDateTime expectedEndTime = endTaskStartTime.plus(endTaskDuration);

        Epic epic = new Epic("Epic", "");
        epic.addSubtask(new Subtask("middle task", "",
                LocalDateTime.of(2025,1,2,0,0),middleTaskDuration));
        epic.addSubtask(new Subtask("end task","", endTaskStartTime, endTaskDuration));
        epic.addSubtask(new Subtask("start task", "", startTime, startTaskDuration));

        assertEquals(startTime, epic.getStartTime(), "Не верно установился startTime");
        assertEquals(expectedEndTime, epic.getEndTime(), "Не верно установился endTime");
        assertEquals(expectedDuration, epic.getDuration(), "Не верно установился duration");
    }

    @Test
    void setTimeWithUpdateSubtask() {
        LocalDateTime startTime = LocalDateTime.of(2025,1,1,0,0);
        Duration startTaskDuration = Duration.ofHours(1);
        Subtask task = new Subtask("start task", "", startTime, startTaskDuration);
        Epic epic = new Epic("Epic", "");
        epic.addSubtask(task);

        Subtask updatedNullTimeTask = new Subtask("null time task", "");
        epic.updateSubtask(task, updatedNullTimeTask);
        assertNull(epic.getStartTime(), "start time должен быть null");
        assertNull(epic.getEndTime(), " end time должен быть null");
        assertNull(epic.getDuration(), "duration не задан и должен быть null");

        LocalDateTime updatedStartTime = LocalDateTime.of(2024,12,31,20,0);
        Duration updatedDuration = Duration.ofHours(2);
        Subtask updatedTask = new Subtask("updated time task","", updatedStartTime, updatedDuration);
        epic.updateSubtask(task, updatedTask);

        assertEquals(updatedStartTime, epic.getStartTime(), "Не обновился startTime");
        assertEquals(updatedStartTime.plus(updatedDuration), epic.getEndTime(), "Не обновился endTime");
        assertEquals(updatedDuration, epic.getDuration(), "Не обновился duration");
    }
}
