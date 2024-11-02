package model;

public class Subtask extends Task {
    private Epic epic;

    public Subtask() {
        epic = new Epic();
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        String result = "Subtask{" +
                "id='" + id + "'" +
                ", EpicId='" + epic.id + "'" +
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
