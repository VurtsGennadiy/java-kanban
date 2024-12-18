package model;

public class Subtask extends Task {
    private Epic epic;

    public Subtask() {
    }

    public Subtask(String name, String description) {
        super(name, description);
        epic = new Epic();
        epic.addSubtask(this);
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        if (epic != null) {
            epic.addSubtask(this);
        }
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
        if (epic != null && !epic.getSubtasks().contains(this)) {
            epic.addSubtask(this);
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        String result = "Subtask{" +
                "id='" + id + "'" +
                ", EpicId='" + (epic != null ? epic.id : "null") + "'" +
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
