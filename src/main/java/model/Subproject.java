package model;

import java.time.LocalDateTime;

public class Subproject {
    private int subprojectId;
    private String subprojectName;
    private String subprojectDescription;
    private LocalDateTime subprojectDeadline;
    private LocalDateTime subprojectStartDate;
    private double subprojectHours;

    public int getSubprojectId() {
        return subprojectId;
    }

    public void setSubprojectId(int subprojectId) {
        this.subprojectId = subprojectId;
    }

    public String getSubprojectName() {
        return subprojectName;
    }

    public void setSubprojectName(String subprojectName) {
        this.subprojectName = subprojectName;
    }

    public String getSubprojectDescription() {
        return subprojectDescription;
    }

    public void setSubprojectDescription(String subprojectDescription) {
        this.subprojectDescription = subprojectDescription;
    }

    public LocalDateTime getSubprojectDeadline() {
        return subprojectDeadline;
    }

    public void setSubprojectDeadline(LocalDateTime subprojectDeadline) {
        this.subprojectDeadline = subprojectDeadline;
    }

    public LocalDateTime getSubprojectStartDate() {
        return subprojectStartDate;
    }

    public void setSubprojectStartDate(LocalDateTime subprojectStartDate) {
        this.subprojectStartDate = subprojectStartDate;
    }

    public double getSubprojectHours() {
        return subprojectHours;
    }

    public void setSubprojectHours(double subprojectHours) {
        this.subprojectHours = subprojectHours;
    }
}
