package repository;

import manager.ConnectionManager;
import model.Project;
import model.Subproject;
import model.Task;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.PSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Repository
public class Repository {
    @Value("${spring.datasource.url}")
    private String db_url;
    @Value("${spring.application.name}")
    private String username;
    @Value("${spring.datasource.password}")
    private String pwd;
    private Connection connection = ConnectionManager.getConnection(db_url, username, pwd);
    public void test(){
        System.out.println(username);
    }


    /**HENT PROJECT**/
    //verdens længste metode :)))) den skal måske deles op
    public Project getProject(int projectId) {
        Project project = null;
        List<Task> tasks = new ArrayList<>();
        List<Subproject> subprojects = new ArrayList<>();

        try {
            //Projects
            String projectSQL = "SELECT projects_id, project_name, project_description, total_hours, project_deadline " +
                    "FROM projects " +
                    "WHERE projects_id = ?;";

            try (PreparedStatement projectStatement = connection.prepareStatement(projectSQL)) {
                projectStatement.setInt(1, projectId);
                ResultSet projectResult = projectStatement.executeQuery();
                if (projectResult.next()) {
                    int id = projectResult.getInt("projects_id");
                    String name = projectResult.getString("project_name");
                    String description = projectResult.getString("project_description");
                    double totalHours = projectResult.getDouble("total_hours");
                    Date deadline = projectResult.getDate("project_deadline");
                    project = new Project(id, name, description, tasks, subprojects, totalHours, deadline);
                }
            }

            //Subprojects
            String subprojectSQL = "SELECT subproject_id, subproject_name, subproject_description, subproject_hours, subproject_deadline " +
                    "FROM subprojects " +
                    "WHERE parent_project_id = ?;";

            try (PreparedStatement subprojectStatement = connection.prepareStatement(subprojectSQL)) {
                subprojectStatement.setInt(1, projectId);
                ResultSet subprojectResult = subprojectStatement.executeQuery();
                while (subprojectResult.next()) {
                    int id = subprojectResult.getInt("subproject_id");
                    String name = subprojectResult.getString("subproject_name");
                    String description = subprojectResult.getString("subproject_description");
                    double hours = subprojectResult.getDouble("subproject_hours");
                    Date deadline = subprojectResult.getDate("subproject_deadline");
                    subprojects.add(new Subproject(id, name, description, hours, deadline));
                }
            }

            //Tasks
            if (project != null) {
                String taskSQL = "SELECT task_id, task_name, task_description, task_hours, task_deadline " +
                        "FROM tasks " +
                        "INNER JOIN subprojects ON tasks.subproject_id = subprojects.subproject_id" +
                        "WHERE parent_project_id = ?;"; //gør det noget at where ikke er fed??

                try (PreparedStatement taskStatement = connection.prepareStatement(taskSQL)) {
                    taskStatement.setInt(1, projectId);
                    ResultSet taskResult = taskStatement.executeQuery();
                    while (taskResult.next()) {
                        int id = taskResult.getInt("task_id");
                        String name = taskResult.getString("task_name");
                        String description = taskResult.getString("task_description");
                        double hours = taskResult.getDouble("task_hours");
                        Date deadline = taskResult.getDate("task_deadline");
                        tasks.add(new Task(id, name, description, deadline, hours));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return project;
    }



    /**OPRET PROJECT**/
    public void createProject(String name, String description, Date deadline) {
        try {
            String SQL = "INSERT INTO projects (project_name, project_description, total_hours, deadline)" +
                    "VALUES (?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, deadline);
            preparedStatement.executeQuery();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**EDIT PROJECT**/
    public void editProject(int projectId,Project updatedProject){
        try {
            String SQL = "UPDATE projects " + "SET project_name = ?, project_description = ?, project_deadline = ? " +
                    "WHERE project_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(4,projectId);
            preparedStatement.setString(1, updatedProject.getName());
            preparedStatement.setString(2, updatedProject.getDescription());
            preparedStatement.setDate(3, updatedProject.getDeadline());
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**SLET PROJECT**/
    public void deleteProject(int projectId){
        try {
            String SQL = "DELETE FROM projects" +
                    "WHERE projects_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, projectId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }



    /**OPRET SUBPROJECT**/
    public void createSubproject(String name, String description, double hours, Date deadline, int parentProjectId){
        try {
            String SQL = "INSERT INTO subprojects (subproject_name, subproject_description, subproject_hours, subproject_deadline, parent_project_id)" +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, hours);
            preparedStatement.setDate(4, deadline);
            preparedStatement.setInt(5, parentProjectId);
            preparedStatement.executeQuery();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**OPRET TASK**/
    public void createTask(String name, String description, double hours, Date deadline, int subprojectId){
        try {
            String SQL = "INSERT INTO tasks (task_name, task_description, task_hours, task_deadline, subproject_id)" +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, hours);
            preparedStatement.setDate(4, deadline);
            preparedStatement.setInt(5, subprojectId);
            preparedStatement.executeQuery();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**SLET SUBPROJECT**/
    public void deleteSubproject(int subprojectId){
        try {
            String SQL = "DELETE FROM subprojects" +
                    "WHERE subprojects_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, subprojectId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }



    /**OPRET SUBTASKS**/
    public void createSubtask(String name, String description, double hours, Date deadline, int parentTaskId){
        try {
            String SQL = "INSERT INTO subtasks (subtask_name, subtask_description, subtask_hours, subtask_deadline, parent_task_id)" +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, hours);
            preparedStatement.setDate(4, deadline);
            preparedStatement.setInt(5, parentTaskId);
            preparedStatement.executeQuery();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }




}
