package projectmanagement.model;

import java.sql.Date;
import java.util.List;

public class Project {
    private int id;
    private String name;
    private String description;
    private List<Subproject> subprojects;
    private double totalHours;
    private Date startDate;
    private Date deadline;
    private Status status;


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Project() {} //til thymeleaf

    public Project(int id, String name, String description, List<Subproject> subprojects,
                   double totalHours, Date startDate, Date deadline, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subprojects = subprojects;
        this.totalHours = totalHours;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
    }

    public Project(int id, String name, String description, double totalHours, Date startDate, Date deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
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

    public List<Subproject> getSubprojects() {
        return subprojects;
    }

    public void setSubprojects(List<Subproject> subprojects) {
        this.subprojects = subprojects;
    }
    public double getTotalHours() {
        return totalHours;
    }


    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
