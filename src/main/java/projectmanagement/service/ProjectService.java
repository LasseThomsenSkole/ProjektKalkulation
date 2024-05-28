package projectmanagement.service;


import org.springframework.beans.factory.annotation.Autowired;
import projectmanagement.model.*;
import org.springframework.stereotype.Service;
import projectmanagement.repository.ProjectRepository;
import projectmanagement.repository.UserRepository;

import java.sql.Date;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Project> findAllProjects() {
        return projectRepository.getAllProjects();
    }

    public List<Project> findAllProjectsSorted(String sort) {
        return projectRepository.getAllProjectsSorted(sort);
    }

    public int createProject(String name, String description, Date startDate, Date deadline) {
         return projectRepository.createProject(name, description, startDate, deadline);
    }


    public Project getProject(int id) {
        return projectRepository.getProject(id);
    }

    public  Subproject getSubprojectById(int id){
        return projectRepository.getSubprojectById(id);
    }

    public Task getTaskById(int id){
        return projectRepository.getTaskById(id);
    }

    public Subtask getSubtaskById(int id){
        return projectRepository.getSubtaskById(id);
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

    public List<Project> findArchivedProjects() {
        return projectRepository.findArchivedProjects();
    }

    public void changeProjectStatus(int projectId, Status newStatus) {
        projectRepository.changeProjectStatus(projectId, newStatus);
    }
    
    public void changeSubprojectStatus(int subprojectId, Status newStatus) {
        projectRepository.changeSubprojectStatus(subprojectId, newStatus);
    }

    public void changeTaskStatus(int taskId, Status newStatus) {
        projectRepository.changeTaskStatus(taskId, newStatus);
    }

    public int createSubproject(String name, String description, Date startDate, Date deadline, int parentProjectId) {
        return projectRepository.createSubproject(name, description, startDate, deadline, parentProjectId);
    }

    public int createTask(String name, String description, Date startDate, Date deadline, int subprojectId) {
        return projectRepository.createTask(name, description, startDate, deadline, subprojectId);
    }
    
    public List<Project> getProjectsForUser(int userId) {
        return projectRepository.getProjectsFromAssignedUser(userId);
    }

    public void assignUserToProject(int userId, int projectId){
        userRepository.assignUserToProject(userId, projectId);
    }

    public int findSubprojectIdByTaskId(int taskId) {
        return projectRepository.findSubprojectIdByTaskId(taskId);
    }

    public int findParentProjectIdBySubprojectId(int subprojectId) {
        return projectRepository.findParentProjectIdBySubprojectId(subprojectId);
    }

    public void deleteProject(int projectId) {
        projectRepository.deleteProject(projectId);
    }

    public void deleteTask(int taskId) {
        projectRepository.deleteTask(taskId);
    }

    public void deleteSubtask(int subtaskId) {
        projectRepository.deleteSubtask(subtaskId);
    }

    public void deleteSubproject(int subprojectId) {
        projectRepository.deleteSubproject(subprojectId);
    }

    public void createSubtask(String name, String description, double hours, Date startDate, Date deadline, int parentTaskId) {
      projectRepository.createSubtask(name, description, hours, startDate, deadline, parentTaskId);
    }
    public void changeSubtaskStatus(int subtaskId, Status status) {
        projectRepository.changeSubtaskStatus(subtaskId, status);
    }
    public int findTaskIdBySubtaskId(int subtaskId) {
        return projectRepository.findTaskIdBySubtaskId(subtaskId);
    }

}
