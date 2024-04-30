package repository;

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

    public void test(){
        System.out.println(username);
    }


    /**HENT PROJECT**/
    //verdens længste metode :)))) den skal måske deles op
    public Project getProject(int projectId) {
        Project project = null;
        List<Task> tasks = new ArrayList<>();
        List<Subproject> subprojects = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(db_url, username, pwd)) {

            //Projects
            String projectSQL = "SELECT projects_id, project_name, project_description, total_hours, project_deadline " +
                    "FROM projects " +
                    "WHERE projects_id = ?;";

            try (PreparedStatement projectStatement = con.prepareStatement(projectSQL)) {
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

            try (PreparedStatement subprojectStatement = con.prepareStatement(subprojectSQL)) {
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

                try (PreparedStatement taskStatement = con.prepareStatement(taskSQL)) {
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
    public void createProject(String name, String description, double totalHours, Date deadline) {
        try (Connection connection = DriverManager.getConnection(db_url, username, pwd)){
            String SQL = "INSERT INTO projects (project_name, project_description, total_hours, deadline)" +
                    "VALUES (?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, totalHours);
            preparedStatement.setDate(4, deadline);
            preparedStatement.executeQuery();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }




    /**TOTALHOURS SUM TIL PROJECT**/ //så lige lasse har lavet det her i databasen hahaha
    /*public double getTotalHours(int projectId){
        double totalHours = 0;
        try (Connection connection = DriverManager.getConnection(db_url, username, pwd)){

            //subprojects
            String subprojectSQL = "SELECT SUM subprojects.subproject_hours AS total_subproject_hours" +
                    "FROM subprojects" +
                    "WHERE parent_project_id = ?";
            try (PreparedStatement subprojectStatement = connection.prepareStatement(subprojectSQL)){
                subprojectStatement.setDouble(1, projectId);
                ResultSet subprojectResult = subprojectStatement.executeQuery();
                if(subprojectResult.next()){
                    totalHours += subprojectResult.getDouble("total_subproject_hours");
                }
            }

            //TODO
            //tasks
            String taskSQL = "SELECT SUM tasks.task_hours AS total_task_hours" +
                    "FROM tasks" +
                    "JOIN ";
            try (PreparedStatement taskStatement = connection.prepareStatement(taskSQL)){
                taskStatement.setDouble(1, projectId);
                ResultSet taskResult = taskStatement.executeQuery();
                if(taskResult.next()){
                    totalHours += taskResult.getDouble("total_task_hours");
                }
            }

            //TODO
            //subtasks
            String subtaskSQL = "SELECT SUM subtasks-subtask_hours AS total_subtask_hours" +
                    "FROM subtasks";
            try (PreparedStatement subtaskStatement = connection.prepareStatement(subtaskSQL)){
                subtaskStatement.setDouble(1, projectId);
                ResultSet subtaskResult = subtaskStatement.executeQuery();
                if(subtaskResult.next()){
                    totalHours += subtaskResult.getDouble("total_subtask_hours");
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return totalHours;
    }*/
}
