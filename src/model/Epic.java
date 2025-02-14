package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Epic extends Task {
    private final List<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic() {
        this("","");
    }

    public Epic(String name, String description) {
        super(name, description);
        subtasksId = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtask.setEpicId(this.id);
        subtasksId.add(subtask.getId());
        updateTime(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        if (!subtasksId.contains(subtask.getId())) {
            throw new IllegalArgumentException("Сабтаска с id = " + subtask.getId() + " не содержится в эпике");
        }
        subtasksId.remove(Integer.valueOf(subtask.getId())); // обертка Integer для удаления по содержимому, а не по индексу
        removeTime(subtask);
    }

    public List<Integer> getSubtasksId() {
        return List.copyOf(subtasksId);
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
        joiner.add("description.length=" + description.length());
        joiner.add("subtasks.count=" + subtasksId.size());
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
