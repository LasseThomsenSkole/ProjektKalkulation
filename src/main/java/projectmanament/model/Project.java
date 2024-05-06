package projectmanament.model;

import java.sql.Date;
import java.util.List;

public class Project {
    private int id;
    private String name;
    private String description;
    private List<Task> tasks;
    private List<Subproject> subprojects;
    private double totalHours;
    private Date deadline;
    private Status status;


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Project() {} //til thymeleaf

    public Project(int id, String name, String description, List<Task> tasks, List<Subproject> subprojects,
                   double totalHours, Date deadline, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
        this.subprojects = subprojects;
        this.totalHours = totalHours;
        this.deadline = deadline;
        this.status = status;
    }

    public Project(int id, String name, String description, double totalHours, Date deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalHours = totalHours;
        this.deadline = deadline;
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

    public List<Subproject> getSubprojects() {
        return subprojects;
    }

    public void setSubprojects(List<Subproject> subprojects) {
        this.subprojects = subprojects;
    }
    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
