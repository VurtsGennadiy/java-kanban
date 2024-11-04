package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

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
            subtask.setEpic(this);
        }
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        subtask.setEpic(this);
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
}
