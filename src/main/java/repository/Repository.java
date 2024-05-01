package repository;

import manager.ConnectionManager;
import model.Project;
import model.Subproject;
import model.Subtask;
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
    public Project getProject(int projectId) {
        Project project = null;
        List<Task> tasks = new ArrayList<>();
        List<Subproject> subprojects = new ArrayList<>();

        try {
            String SQL = "SELECT projects_id, project_name, project_description, total_hours, project_deadline " +
                    "FROM projects " +
                    "WHERE projects_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, projectId);
                ResultSet projectResult = preparedStatement.executeQuery();
                if (projectResult.next()) {
                    int id = projectResult.getInt("projects_id");
                    String name = projectResult.getString("project_name");
                    String description = projectResult.getString("project_description");
                    double totalHours = projectResult.getDouble("total_hours");
                    Date deadline = projectResult.getDate("project_deadline");
                    project = new Project(id, name, description, tasks, subprojects, totalHours, deadline);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return project;
    }



    /**HENT SUBPROJECT**/
    public List<Subproject> getSubprojects(int subprojectId){
        List<Subproject> subprojects = new ArrayList<>();
        try {
            String SQL = "SELECT subproject_id, subproject_name, subproject_description, subproject_hours, subproject_deadline " +
                    "FROM subprojects " +
                    "WHERE parent_project_id = ?;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, subprojectId);
                ResultSet subprojectResult = preparedStatement.executeQuery();
                while (subprojectResult.next()) {
                    int id = subprojectResult.getInt("subproject_id");
                    String name = subprojectResult.getString("subproject_name");
                    String description = subprojectResult.getString("subproject_description");
                    double hours = subprojectResult.getDouble("subproject_hours");
                    Date deadline = subprojectResult.getDate("subproject_deadline");
                    subprojects.add(new Subproject(id, name, description, hours, deadline));
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return subprojects;
    }



    /**HENT TASK**/
    public List<Task> getTasks(int taskId){
        List<Task> tasks = new ArrayList<>();
        try {
            String taskSQL = "SELECT task_id, task_name, task_description, task_hours, task_deadline " +
                    "FROM tasks " +
                    "WHERE subproject_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(taskSQL)) {
                preparedStatement.setInt(1, taskId);
                ResultSet taskResult = preparedStatement.executeQuery();
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
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return tasks;
    }



    /**HENT SUBTASK**/
    public List<Subtask> getSubtasks(int subtaskId){
        List<Subtask> subtasks = new ArrayList<>();
        try {
            String taskSQL = "SELECT subtask_id, subtask_name, subtask_description, subtask_hours, subtask_deadline " +
                    "FROM subtasks " +
                    "WHERE parent_task_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(taskSQL)) {
                preparedStatement.setInt(1, subtaskId);
                ResultSet taskResult = preparedStatement.executeQuery();
                while (taskResult.next()) {
                    int id = taskResult.getInt("subtask_id");
                    String name = taskResult.getString("subtask_name");
                    String description = taskResult.getString("subtask_description");
                    double hours = taskResult.getDouble("subtask_hours");
                    Date deadline = taskResult.getDate("subtask_deadline");
                    subtasks.add(new Subtask(id, name, description, deadline, hours));
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return subtasks;
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



    /**SLET PROJECT**/ //todo gør så den sletter det som hører til også
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

//zjhdgaSHJDGASHDJASD

    /**DELETE CHECK SUBPROJECT**/
    public boolean isSubprojectDeletedFromProjects(int subprojectId){
        try {
            String SQL = "SELECT count(*) AS count" +
                    "FROM subprojects" +
                    "LEFT JOIN projects ON subprojects.parent_project_id = projects.project_id" +
                    "WHERE subprojects.subproject_id = ? AND projects.project_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, subprojectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt("count");
                return count == 0; // return true hvis det er ikke er noget
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return false;
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



    /**SLET TASK**/
    public void deleteTask(int taskId){
        try {
            String SQL = "DELETE FROM tasks" +
                    "WHERE task_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, taskId);
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


    /**SLET SUBTASK**/
    public void deleteSubtask(int subtaskId){
        try {
            String SQL = "DELETE FROM subtasks" +
                    "WHERE subtask_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, subtaskId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }




}
