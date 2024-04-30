package model;

import java.util.Date;

public class Subtask {
    private int id;
    private String name;
    private String description;
    private Date deadline;
    private double hours;
    public Subtask() {} //thymeleaf

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

    public Subtask(int id, String name, String description, Date deadline, double hours) {
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
}
