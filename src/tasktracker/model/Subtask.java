package tasktracker.model;

public class Subtask extends Task
{
    private final int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId)
    {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId()
    {
        return epicId;
    }

    @Override
    public String toString()
    {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}