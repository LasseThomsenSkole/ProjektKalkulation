package projectmanament.model;

import java.sql.Date;
import java.util.List;

public class Subproject {
    private int id;
    private String name;
    private String description;
    private double hours;
    private Date deadline;
    private Status status;
    private List<Task> tasks;

    public Subproject() {} // Thymeleaf kræver en no-args konstruktør

    public Subproject(int id, String name, String description, double hours, Date deadline, Status status, List<Task> tasks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hours = hours;
        this.deadline = deadline;
        this.status = status;
        this.tasks = tasks;
    }

    public Subproject(int id, String name, String description, double hours, Date deadline, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hours = hours;
        this.deadline = deadline;
        this.status = status;
    }

    // Getters og setters for alle felter
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

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
