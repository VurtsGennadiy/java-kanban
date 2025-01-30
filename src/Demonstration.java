import model.*;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        manager.removeTask(4);
        printAllTasks();
        printHistory();
        System.out.println();

        System.out.println("***** Удаляем эпик из 3-х подзадач id=5, проверяем историю *****");
        manager.removeTask(5);
        printAllTasks();
        printHistory();
        System.out.println();

        try {
            Files.delete(Path.of("Demonstration.csv"));
        } catch (IOException e) {
            e.printStackTrace();;
        }
    }

    void createTasks() {
        Task task1 = new Task("Task1_Name","Task1_Description",
                LocalDateTime.of(2025,1,1,10,0), Duration.ofMinutes(30));
        Task task2 = new Task("Task2_Name","Task2_Description");

        Subtask subtask1 = new Subtask("Subtask1_Name", "Subtask1_OfEpic1",
                LocalDateTime.of(2025,1,2,9,0), Duration.ofHours(1));
        Subtask subtask2 = new Subtask("Subtask2_Name", "Subtask2_OfEpic1",
                LocalDateTime.of(2025, 1,3,10,0), Duration.ofDays(1));
        Subtask subtask3 = new Subtask("Subtask3_Name", "Subtask3_OfEpic1");
        ArrayList<Subtask> subtasksOfEpic1 = new ArrayList<>();
        subtasksOfEpic1.add(subtask1);
        subtasksOfEpic1.add(subtask2);
        subtasksOfEpic1.add(subtask3);

        Epic epicWith3subs = new Epic("epicWith3subs", "Epic_Of_Three_Subtasks", subtasksOfEpic1);
        Epic emptyEpic = new Epic("emptyEpic", "Empty_Subtasks_List");

        manager.createNewTask(task1);
        manager.createNewTask(task2);
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);
        manager.createNewSubtask(subtask3);
        manager.createNewEpic(epicWith3subs);
        manager.createNewEpic(emptyEpic);
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
        manager.getSubtask(2);
        manager.getSubtask(3);
        manager.getSubtask(4);
        manager.getEpic(5);
        manager.getEpic(6);
    }

    void reverseOrderAddInHistory() {
        manager.getEpic(6);
        manager.getEpic(5);
        manager.getSubtask(4);
        manager.getSubtask(3);
        manager.getSubtask(2);
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
