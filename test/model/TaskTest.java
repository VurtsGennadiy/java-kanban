package model;

import org.junit.jupiter.api.Test;
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
}
