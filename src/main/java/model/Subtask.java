package model;

import java.util.Date;

public class Subtask {
    private int id;
    private String title;
    private String description;
    private Date deadline;
    private int totalHours;
    public Subtask() {} //thymeleaf

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public Subtask(int id, String title, String description, Date deadline, int totalHours) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.totalHours = totalHours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
