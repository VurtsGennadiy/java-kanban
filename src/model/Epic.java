package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic() {
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, ArrayList<Subtask> subtasks) {
        super(name, description);
        this.subtasks = subtasks;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
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
