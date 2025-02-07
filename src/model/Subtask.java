package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.StringJoiner;

public class Subtask extends Task {
    private int epicId;

    public Subtask() {
        this("","", null, null);
    }

    public Subtask(String name, String description) {
        this(name, description, null, null);
    }

    public Subtask(String name, String description, int epicId) {
        this(name, description, null, null, epicId);
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        epicId = -1;
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "Subtask{", "}");
        joiner.add("id=" + id);
        joiner.add("EpicId=" + epicId);
        joiner.add("status='" + status + "'");
        joiner.add("name='" + name + "'");
        joiner.add("description.length=" + description.length());
        joiner.add("startTime=" + startTime);
        joiner.add("duration=" + duration);
        return joiner.toString();
    }
}
