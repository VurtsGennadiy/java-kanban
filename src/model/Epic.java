package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
        super(name, description);
        this.subtasks = subtasks;
        for (Subtask subtask : subtasks) {
            if (subtask.getEpic() != this) {
                subtask.setEpic(this);
            }
            updateTime(subtask);
        }
    }

    public void addSubtask(Subtask subtask) {
        if (!subtasks.contains(subtask)) {
            subtasks.add(subtask);
        }
        subtask.setEpic(this);
        updateTime(subtask);
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
        String result = "Epic{" +
                "id='" + id + "'" +
                ", status=" + status +
                ", name='" + name + '\'';
        if (description != null) {
            result += ", description.length='" + description.length() + "'";
        } else {
            result += ", description=null";
        }
        result += ", subtasks.count='" + subtasks.size() + "'}";
        return result;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void updateTime(Subtask subtask) {
        LocalDateTime newTaskStartTime = subtask.getStartTime();
        Duration newTaskDuration = subtask.getDuration();
        LocalDateTime newTaskEndTime = subtask.getEndTime();
        if (startTime == null || newTaskStartTime == null || startTime.isAfter(newTaskStartTime)) {
            startTime = newTaskStartTime;
        }

        if (duration == null) {
            duration = newTaskDuration;
        } else if (newTaskDuration != null) {
            duration = duration.plus(newTaskDuration); // plus null
        }

        if (endTime == null || newTaskEndTime == null || newTaskEndTime.isAfter(endTime)) {
            endTime = newTaskEndTime;
        }
    }

    private void removeTime(Subtask oldSubtask) {
        if (duration != null) {
            duration = duration.minus(oldSubtask.getDuration());
            if (duration.isZero()) {
                duration = null;
            }
        }
        if (startTime != null && startTime.isEqual(oldSubtask.getStartTime())) {
            startTime = null;
        }
        if (endTime != null && endTime.isEqual(oldSubtask.getEndTime())) {
            endTime = null;
        }
    }
}
