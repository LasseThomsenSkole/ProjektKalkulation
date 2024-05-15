package projectmanament.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projectmanament.model.Project;
import projectmanament.model.Subproject;
import projectmanament.model.Task;
import projectmanament.repository.ProjectRepository;

import java.util.List;

@Service
public class AssignmentService {
    @Autowired
    private ProjectRepository projectRepository;

    // Assign a user to a project
    public void assignUserToProject(int userId, int projectId) {
        projectRepository.assignUserToProject(userId, projectId);
    }

    // Get all projects assigned to a user
    public List<Project> getProjectsForUser(int userId) {
        return projectRepository.getProjectsFromAssignedUser(userId);
    }

    // Assign a user to a subproject
    public void assignUserToSubproject(int userId, int subprojectId) {
        projectRepository.assignUserToSubproject(userId, subprojectId);
    }

    // Get all subprojects assigned to a user
    public List<Subproject> getSubprojectsForUser(int userId) {
        return projectRepository.getSubprojectsFromUser(userId);
    }

    // Assign a user to a task
    public void assignUserToTask(int userId, int taskId) {
        projectRepository.assignUserToTask(userId, taskId);
    }

    // Get all tasks assigned to a user
    public List<Task> getTasksForUser(int userId) {
        return projectRepository.getTasksFromUser(userId);
    }
}
