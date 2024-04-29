package model;

import java.time.LocalDateTime;

public class Project {
    private int projectId;
    private String projectName;
    private String projectDescription;
    private LocalDateTime projectDeadline;
    private LocalDateTime projectStartDate;
    private double totalHours;

    public Project (int projectId, String projectName, String projectDescription, LocalDateTime projectDeadline,
                    LocalDateTime projectStartDate, double totalHours){
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectDeadline = projectDeadline;
        this.projectStartDate = projectStartDate;
        this.totalHours = totalHours;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public LocalDateTime getProjectDeadline() {
        return projectDeadline;
    }

    public void setProjectDeadline(LocalDateTime projectDeadline) {
        this.projectDeadline = projectDeadline;
    }

    public LocalDateTime getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDateTime projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }
}
