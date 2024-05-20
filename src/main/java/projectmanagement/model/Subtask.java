package projectmanagement.model;

import java.sql.Date;

public class Subtask {
    private int id;
    private String name;
    private String description;
    private Date startDate;
    private Date deadline;
    private double hours;
    private Status status;

    public Subtask() {} //thymeleaf

    public Subtask(int id, String name, String description, double hours, Date startDate, Date deadline, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hours = hours;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
    }

    public Subtask(int id, String name, String description, Date startDate, java.sql.Date deadline, double hours) {
    }

    public Date getDeadline() {
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

    public Subtask(int id, String name, String description, Date startDate, Date deadline, double hours, Status status) {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
