package model;

import java.util.Objects;
import java.util.StringJoiner;
import java.time.LocalDateTime;
import java.time.Duration;

public class Task {
    protected int id;
    protected TaskStatus status;
    protected String name;
    protected String description;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task() {
        status = TaskStatus.NEW;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = TaskStatus.NEW;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this(name, description);
        this.startTime = startTime;
        if (duration != null &&  durationValidate(duration)) {
            this.duration = duration;
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime endTime = null;
        if (startTime != null && duration != null) {
            endTime = startTime.plus(duration);
        }
        return endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "Task{", "}");
        joiner.add("id=" + id);
        joiner.add("status='" + status + "'");
        joiner.add("name='" + name + "'");
        joiner.add("description=" + (description == null ? "null" : description.length()));
        joiner.add("startTime=" + startTime);
        joiner.add("duration=" + duration);
        return joiner.toString();
    }

    public boolean isIntersect(Task other) {
        if (this.getEndTime() == null || other.getEndTime() == null) {
            return false;
        }
        // this.start ---- this.end   other.start ---- other.end
        boolean isBefore = !this.getEndTime().isAfter(other.getStartTime());
        // other.start ---- other.end   this.start ---- this.end
        boolean isAfter = !this.getStartTime().isBefore(other.getEndTime());
        return !(isBefore || isAfter);
    }

    private boolean durationValidate(Duration duration) {
        if (duration.getSeconds() <= 0) {
            throw new IllegalArgumentException("Длительность задачи должна быть положительная: "
                    + duration.getSeconds());
        }
        return true;
    }
}
