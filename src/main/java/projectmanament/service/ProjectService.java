package projectmanament.service;


import org.springframework.beans.factory.annotation.Autowired;
import projectmanament.model.*;
import org.springframework.stereotype.Service;
import projectmanament.repository.ProjectRepository;

import java.sql.Date;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> findAllProjects() {
        return projectRepository.findAllProjects();
    }

    public void createProject(String name, String description, Date deadline) {
        projectRepository.createProject(name, description, deadline);
    }

    public Project getProject(int id) {
        return projectRepository.getProject(id);
    }

    public  Subproject getSubprojectById(int id){
        return projectRepository.getSubprojectById(id);
    }

    public void editProject(int id, Project updatedProject){
        projectRepository.editProject(id, updatedProject);
    }

    public void editSubproject(int id, Subproject updatedSubproject){
        projectRepository.editSubproject(id, updatedSubproject);
    }

    public void editTask(int id, Task updatedTask){
        projectRepository.editTask(id, updatedTask);
    }

    public void editSubtask(int id, Subtask updatedSubtask){
        projectRepository.editSubtask(id,updatedSubtask);
    }

    public List<Project> findAllProjectsByStatus(Status status) {
        return projectRepository.findAllProjectsByStatus(status);
    }

    public List<Project> findArchivedProjects() {
        return projectRepository.findArchivedProjects();
    }

    public void changeProjectStatus(int projectId, Status newStatus) {
        projectRepository.changeProjectStatus(projectId, newStatus);
    }

    // Ændre status for et subprojekt
    public void changeSubprojectStatus(int subprojectId, Status newStatus) {
        projectRepository.changeSubprojectStatus(subprojectId, newStatus);
    }

    // Ændre status for en opgave (task)
    public void changeTaskStatus(int taskId, Status newStatus) {
        projectRepository.changeTaskStatus(taskId, newStatus);
    }

    // Ændre status for en subopgave (subtask)
    public void changeSubtaskStatus(int subtaskId, Status newStatus) {
        projectRepository.changeSubtaskStatus(subtaskId, newStatus);
    }


}
