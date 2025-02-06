package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.StringJoiner;

public class Subtask extends Task {
    private Epic epic;
    private int epicId;

    public Subtask() {
    }

    public Subtask(String name, String description) {
        super(name, description);
        epic = new Epic();
        epic.addSubtask(this);
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        setEpic(epic);
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        epic = new Epic();
        epic.addSubtask(this);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, Epic epic) {
        super(name, description, startTime, duration);
        setEpic(epic);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        // проверяем что метод вызван не из Epic
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        boolean fromEpic = stackTrace[2].getClassName().equals(Epic.class.getName());
        if (!fromEpic && epic != null) {
            epic.addSubtask(this);
        }
        this.epic = epic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "Subtask{", "}");
        joiner.add("id=" + id);
        joiner.add("EpicId=" + (epic == null ? "null" : epic.getId()));
        joiner.add("status='" + status + "'");
        joiner.add("name='" + name + "'");
        joiner.add("description=" + (description == null ? "null" : description.length()));
        joiner.add("startTime=" + startTime);
        joiner.add("duration=" + duration);
        return joiner.toString();
    }
}
