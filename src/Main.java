import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        new Demonstration(taskManager).run();
    }
}
