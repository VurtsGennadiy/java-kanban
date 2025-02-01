package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.StringJoiner;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic() {
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, ArrayList<Subtask> subtasks) {
        this(name, description);
        subtasks.forEach(this::addSubtask);
    }

    public void addSubtask(Subtask subtask) {
        if (subtask.getEpic() == this) {
            return;
        }
        subtasks.add(subtask);
        updateTime(subtask);
        subtask.setEpic(this);
    }

    public void updateSubtask(Subtask oldSubtask, Subtask newSubtask) {
        subtasks.remove(oldSubtask);
        removeTime(oldSubtask);
        addSubtask(newSubtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "Epic{", "}");
        joiner.add("id=" + id);
        joiner.add("status='" + status + "'");
        joiner.add("name='" + name + "'");
        joiner.add("description=" + (description == null ? "null" : description.length()));
        joiner.add("subtasks.count=" + subtasks.size());
        joiner.add("startTime=" + startTime);
        joiner.add("duration=" + duration);
        return joiner.toString();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void updateTime(Subtask subtask) {
        LocalDateTime newTaskStartTime = subtask.getStartTime();
        Duration newTaskDuration = subtask.getDuration();
        LocalDateTime newTaskEndTime = subtask.getEndTime();
        if (startTime == null || (newTaskStartTime != null && startTime.isAfter(newTaskStartTime))) {
            startTime = newTaskStartTime;
        }

        if (duration == null) {
            duration = newTaskDuration;
        } else if (newTaskDuration != null) {
            duration = duration.plus(newTaskDuration);
        }

        if (endTime == null || (newTaskEndTime != null && newTaskEndTime.isAfter(endTime))) {
            endTime = newTaskEndTime;
        }
    }

    private void removeTime(Subtask oldSubtask) {
        if (duration != null && oldSubtask.getDuration() != null) {
            duration = duration.minus(oldSubtask.getDuration());
            if (duration.isZero()) {
                duration = null;
            }
        }
        if (startTime != null && oldSubtask.getStartTime() != null && startTime.isEqual(oldSubtask.getStartTime())) {
            startTime = null;
        }
        if (endTime != null && oldSubtask.getEndTime() != null && endTime.isEqual(oldSubtask.getEndTime())) {
            endTime = null;
        }
    }
}
