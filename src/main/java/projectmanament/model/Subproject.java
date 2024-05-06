package projectmanament.model;

import java.sql.Date;
import java.util.List;

public class Subproject {
    private int id;
    private String name;
    private String description;
    private List<Task> tasks;
    private java.sql.Date deadline;
    private double hours;
    private Status status;

    public Subproject(){} //thymeleaf

    public Subproject(int id, String name, String description, List<Task> tasks, java.sql.Date deadline, int hours, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
        this.deadline = deadline;
        this.hours = hours;
        this.status = status;
    }

    public Subproject(int id, String name, String description, double hours, java.sql.Date deadline, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.hours = hours;
        this.status = status;
    }

    public Subproject(int id, String name, String description, double hours, Date deadline) {
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    public java.sql.Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public Status getStatus() {
        return status;
    }
}
