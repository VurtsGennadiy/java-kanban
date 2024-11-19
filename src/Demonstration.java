import model.*;
import service.TaskManager;
import java.util.ArrayList;

public class Demonstration {
    TaskManager manager;

    Demonstration(TaskManager manager) {
        this.manager = manager;
    }

    public void run() {
        System.out.println("***** Создаём задачи всех типов *****");
        createTasks();
        printAllTasks();
        System.out.println();

        System.out.println("***** Просматриваем все задачи, добавляем в историю *****");
        addInHistory();
        printAllTasks();
        System.out.println();

        System.out.println("***** Меняем статусы *****");
        changeStatuses();
        printAllTasks();
        System.out.println();

        System.out.println("***** Удаляем несколько задач *****");
        removeTasks();
        printAllTasks();
    }

    void createTasks() {
        Task task1 = new Task("Task1_Name","Task1_Description");
        Task task2 = new Task("Task2_Name","Task1_Description");

        Subtask subtask1 = new Subtask("Subtask1_Name", "Subtask1_OfEpic1");
        Subtask subtask2 = new Subtask("Subtask2_Name", "Subtask2_OfEpic1");
        ArrayList<Subtask> subtasksOfEpic1 = new ArrayList<>();
        subtasksOfEpic1.add(subtask1);
        subtasksOfEpic1.add(subtask2);

        Epic epic1 = new Epic("Epic1_Name", "Epic_Of_Two_Subtasks", subtasksOfEpic1);
        Epic epic2 = new Epic("Epic2_Name", "Epic_Of_One_Subtasks");
        Subtask subtask3 = new Subtask("Subtask3_Name", "Subtask3_OfEpic2_3ParamsConstructor", epic2);

        manager.createNewTask(task1);
        manager.createNewTask(task2);
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);
        manager.createNewSubtask(subtask3);
        manager.createNewEpic(epic1);
        manager.createNewEpic(epic2);
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

    void addInHistory() {
        manager.getTask(0);
        manager.getTask(1);
        manager.getSubtask(2);
        manager.getSubtask(3);
        manager.getSubtask(4);
        manager.getEpic(5);
        manager.getEpic(6);
    }
}
