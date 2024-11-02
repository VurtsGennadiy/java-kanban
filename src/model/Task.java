package model;

import java.util.Objects;

public class Task {
    protected int id;
    protected TaskStatus status;
    protected String name;
    protected String description;

    public Task() {
        status = TaskStatus.NEW;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = TaskStatus.NEW;
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
        String result = "Task{" +
                "id='" + id + "'" +
                ", status=" + status +
                ", name='" + name + '\'';
        if (description != null) {
            result += ", description.length='" + description.length() + "'";
        } else {
            result += ", description=null";
        }
        return result + "}";
    }
}
