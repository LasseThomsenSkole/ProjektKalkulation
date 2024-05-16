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
        return projectRepository.findAllProjects();
    }

    public List<Project> findAllProjectsSorted(String sort) {
        return projectRepository.findAllProjectsSorted(sort);
    }

    public boolean login(String name, String password){ //TODO MÅSKE LAV OM
        User user = userRepository.getUserFromName(name);
        if (user != null){
            return user.getPassword().equals(password);
        }
        return false;
    }
    public User getUserFromName(String name){
        return userRepository.getUserFromName(name);
    }
    public int getIdFromUser(String name, String password){
        return userRepository.getIdFromUser(name, password);
    }
    public void insertUser(String username, String password){
        userRepository.insertUser(username, password);
    }

    public int createProject(String name, String description, Date startDate, Date deadline) {
         return projectRepository.createProject(name, description, startDate, deadline);
    }
    public void createProjectRelation(int userId, int projectId){
        projectRepository.createProjectRelation(userId, projectId);
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

    public boolean userAlreadyExists(String username){
        return userRepository.userAlreadyExists(username);
    }

    public int createSubproject(String name, String description, double hours, Date startDate, Date deadline, int parentProjectId) {
        return projectRepository.createSubproject(name, description, hours, startDate, deadline, parentProjectId);
    }

    public int createTask(String name, String description, double hours, Date startDate, Date deadline, int subprojectId) {
        return projectRepository.createTask(name, description, hours, startDate, deadline, subprojectId);
    }

    public List<Project> getProjectsForUser(int userId) {
        return projectRepository.getProjectsFromAssignedUser(userId);
    }

}
