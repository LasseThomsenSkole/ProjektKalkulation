package model;

import java.time.LocalDateTime;

public class Subtask {
    private int subtaskId;
    private String subtaskName;
    private String subtaskDescription;
    private LocalDateTime subtaskDeadline;
    private LocalDateTime subtaskStartDate;
    private double subtaskHours;

    public int getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(int subtaskId) {
        this.subtaskId = subtaskId;
    }

    public String getSubtaskName() {
        return subtaskName;
    }

    public void setSubtaskName(String subtaskName) {
        this.subtaskName = subtaskName;
    }

    public String getSubtaskDescription() {
        return subtaskDescription;
    }

    public void setSubtaskDescription(String subtaskDescription) {
        this.subtaskDescription = subtaskDescription;
    }

    public LocalDateTime getSubtaskDeadline() {
        return subtaskDeadline;
    }

    public void setSubtaskDeadline(LocalDateTime subtaskDeadline) {
        this.subtaskDeadline = subtaskDeadline;
    }

    public LocalDateTime getSubtaskStartDate() {
        return subtaskStartDate;
    }

    public void setSubtaskStartDate(LocalDateTime subtaskStartDate) {
        this.subtaskStartDate = subtaskStartDate;
    }

    public double getSubtaskHours() {
        return subtaskHours;
    }

    public void setSubtaskHours(double subtaskHours) {
        this.subtaskHours = subtaskHours;
    }
}
