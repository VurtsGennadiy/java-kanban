import model.*;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.nio.file.Path;

public class Demonstration {
    TaskManager manager;

    Demonstration() {
        manager = Managers.getFileBackedTaskManager(Path.of("Demonstration.csv"));
    }

    public void run() {
        System.out.println("***** Создаём задачи всех типов *****");
        createTasks();
        printAllTasks();
        printHistory();
        System.out.println();

        System.out.println("***** Последовательно просматриваем все задачи, добавляем в историю *****");
        directOrderAddInHistory();
        printHistory();
        System.out.println();

        System.out.println("***** Просматриваем все задачи в обратном порядке, добавляем в историю *****");
        reverseOrderAddInHistory();
        printHistory();
        System.out.println();

        System.out.println("***** Рандомно запрашиваем несколько задач, добавляем в историю *****");
        randomAddInHistory();
        printHistory();
        System.out.println();

        System.out.println("***** Удаляем задачу id=0, проверяем историю *****");
        manager.removeTask(0);
        printAllTasks();
        printHistory();
        System.out.println();

        System.out.println("***** Удаляем подзадачу из эпика id=4, проверяем историю *****");
        manager.removeSubtask(4);
        printAllTasks();
        printHistory();
        System.out.println();

        System.out.println("***** Удаляем эпик из 3-х подзадач id=2, проверяем историю *****");
        manager.removeEpic(2);
        printAllTasks();
        printHistory();
        System.out.println();

        try {
            Files.delete(Path.of("Demonstration.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void createTasks() {
        manager.createNewTask(new Task("Task1_Name", "Task1_Description",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30)));
        manager.createNewTask(new Task("Task2_Name", "Task2_Description"));

        Epic epicWith3subs = manager.createNewEpic(new Epic("epicWith3subs", "Epic_Of_Three_Subtasks"));
        int epicId = epicWith3subs.getId();

        manager.createNewSubtask(new Subtask("Subtask1_Name", "Subtask1_OfEpic1",
                LocalDateTime.of(2025, 1, 2, 9, 0), Duration.ofHours(1), epicId));
        manager.createNewSubtask(new Subtask("Subtask2_Name", "Subtask2_OfEpic1",
                LocalDateTime.of(2025, 1, 3, 10, 0), Duration.ofDays(1), epicId));
        manager.createNewSubtask(new Subtask("Subtask3_Name", "Subtask3_OfEpic1", epicId));
        manager.createNewEpic(new Epic("emptyEpic", "Empty_Subtasks_List"));
    }

    void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }

    void printHistory() {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    void changeStatuses() {
        Task task1 = manager.getTask(0);
        Task task2 = manager.getTask(1);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);
        manager.updateTask(task2);


        Subtask subtask1 = manager.getSubtask(2);
        Subtask subtask2 = manager.getSubtask(3);
        Subtask subtask3 = manager.getSubtask(4);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);
        manager.updateTask(subtask1);
        manager.updateTask(subtask2);
        manager.updateTask(subtask3);
    }

    void removeTasks() {
        manager.removeTask(manager.getAllTasks().getFirst().getId());
        manager.removeTask(manager.getAllSubtasks().getFirst().getId());
        manager.removeTask(manager.getAllEpics().getLast().getId());
    }

    void directOrderAddInHistory() {
        manager.getTask(0);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getSubtask(3);
        manager.getSubtask(4);
        manager.getSubtask(5);
        manager.getEpic(6);
    }

    void reverseOrderAddInHistory() {
        manager.getEpic(6);
        manager.getSubtask(5);
        manager.getSubtask(4);
        manager.getSubtask(3);
        manager.getEpic(2);
        manager.getTask(1);
        manager.getTask(0);
    }

    void randomAddInHistory() {
        manager.getSubtask(3);
        manager.getSubtask(3);
        manager.getEpic(6);
        manager.getTask(0);
    }
}
