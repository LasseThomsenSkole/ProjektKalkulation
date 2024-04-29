package model;

import java.util.List;

public class Project {
    private int id;
    private String name;
    private String description;
    private List<Task> tasks;
    private List<Subproject> subprojects;
    public Project() {} //til thymeleaf

    public Project(int id, String name, String description, List<Task> tasks, List<Subproject> subprojects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
        this.subprojects = subprojects;
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
}
