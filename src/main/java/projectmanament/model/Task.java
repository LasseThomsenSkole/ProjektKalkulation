package projectmanament.model;

import java.sql.Date;
import java.util.List;

public class Task {
    private int id;
    private String name;
    private String description;
    private List<Subtask> subtasks;
    private Date startDate;
    private Date deadline;
    private double hours;
    private Status status;

    public Task() {} //thymeleaf

    public Task(int id, String name, String description, List<Subtask> subtasks, Date startDate, Date deadline, double hours, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subtasks = subtasks;
        this.startDate = startDate;
        this.deadline = deadline;
        this.hours = hours;
        this.status = status;
    }

    public Task(int id, String name, String description, Date startDate, Date deadline, double hours, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadline;
        this.hours = hours;
        this.status = status;
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

    public Date getDeadline() {
        return deadline;
    }

    public double getHours() {
        return hours;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
