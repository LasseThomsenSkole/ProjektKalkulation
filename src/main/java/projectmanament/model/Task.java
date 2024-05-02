package projectmanament.model;

import java.util.Date;
import java.util.List;

public class Task {
    private int id;
    private String name;
    private String description;
    private List<Subtask> subtasks;
    private Date deadline;
    private double hours;

    public Task() {} //thymeleaf

    public Task(int id, String name, String description, List<Subtask> subtasks, Date deadline, double hours) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subtasks = subtasks;
        this.deadline = deadline;
        this.hours = hours;
    }

    public Task(int id, String name, String description, Date deadline, double hours) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.hours = hours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
