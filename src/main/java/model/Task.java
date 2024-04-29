package model;

import java.time.LocalDateTime;

public class Task {
    private int taskId;
    private String taskName;
    private String taskDescription;
    private LocalDateTime taskDeadline;
    private LocalDateTime taskStartDate;
    private double taskHours;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public LocalDateTime getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(LocalDateTime taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public LocalDateTime getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(LocalDateTime taskStartDate) {
        this.taskStartDate = taskStartDate;
    }

    public double getTaskHours() {
        return taskHours;
    }

    public void setTaskHours(double taskHours) {
        this.taskHours = taskHours;
    }
}
