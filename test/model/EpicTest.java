package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
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
    void noAddItself() {

    }
}
