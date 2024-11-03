import service.TaskManager;
import model.*;
import java.util.ArrayList;

public class Test {
    static TaskManager manager = new TaskManager();

    public static void run() {
        createTasks();
        printAllTasks();
        System.out.println();
        changeStatuses();
        printAllTasks();
        System.out.println();
        removeTasks();
        printAllTasks();
    }

    static void createTasks() {
        Task task1 = new Task("Task1_Name","Task1_Descr");
        Task task2 = new Task("Task2_Name","Task2_Descr");

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

    static void printAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(manager.getAllTasks());
        allTasks.addAll(manager.getAllSubtasks());
        allTasks.addAll(manager.getAllEpics());
        for (Task task : allTasks) {
            System.out.println(task);
        }
    }

    static void changeStatuses() {
        ArrayList<Task> tasks = manager.getAllTasks();
        Task task1 = tasks.getFirst();
        Task task2 = tasks.get(1);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);

        ArrayList<Epic> epics = manager.getAllEpics();
        ArrayList<Subtask> subtasksOfEpic1 = epics.getFirst().getSubtasks();
        ArrayList<Subtask> subtasksOfEpic2 = epics.get(1).getSubtasks();
        Subtask subtask1 = subtasksOfEpic1.getFirst();
        Subtask subtask2 = subtasksOfEpic1.get(1);
        Subtask subtask3 = subtasksOfEpic2.getFirst();
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);

        manager.updateTask(task1);
        manager.updateTask(task2);
        manager.updateTask(subtask1);
        manager.updateTask(subtask2);
        manager.updateTask(subtask3);
    }

    static void removeTasks() {
        manager.removeTask(manager.getAllTasks().getFirst().getId());
        manager.removeTask(manager.getAllSubtasks().getFirst().getId());
        manager.removeTask(manager.getAllEpics().getLast().getId());
    }
}
