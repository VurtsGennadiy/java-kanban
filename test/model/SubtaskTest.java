package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    void shouldBeNotEqualsWhenNotEqualIds() {
        Subtask subtask1 = new Subtask("Subtask_name", "Subtask_description");
        Subtask subtask2 = new Subtask("Subtask_name", "Subtask_description");
        subtask1.setId(1);
        subtask2.setId(2);
        assertNotEquals(subtask1, subtask2, "Субтаски равны");
        assertNotEquals(subtask2, subtask1, "Субтаски равны");
    }

    @Test
    void shouldBeEqualsWhenEqualIds() {
        Subtask subtask1 = new Subtask("Subtask1_name", "Subtask1_description");
        Subtask subtask2 = new Subtask("Subtask2_name", "Subtask2_description");
        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2, "Субтаски не равны");
        assertEquals(subtask2, subtask1, "Субтаски не равны");
    }

    @Test
    void shouldBeEqualsItself() {
        Subtask subtask = new Subtask("Subtask_name", "Subtask_description");
        subtask.setId(1);
        assertEquals(subtask, subtask, "Субтаска не равна сама себе");
    }
}
