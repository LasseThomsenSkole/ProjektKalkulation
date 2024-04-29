package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Subproject {
    private int id;
    private String name;
    private String description;
    private List<Task> tasks;
    private Date deadline;
    private int totalHours;

    public Subproject(){} //thymeleaf
    public Subproject(int id, String name, String description, List<Task> tasks, Date deadline, int totalHours) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
        this.deadline = deadline;
        this.totalHours = totalHours;
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
}
