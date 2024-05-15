package projectmanament.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import projectmanament.manager.ConnectionManager;
import projectmanament.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    @Value("${spring.datasource.url}")
    private String db_url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String pwd;
    private Connection connection;

    @PostConstruct
    public void init() {
        connection = ConnectionManager.getConnection(db_url, username, pwd);
    }

    public User getUserById(int userId) {
        try {
            String SQL = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String name = resultSet.getString("user_name");
                boolean isAdmin = resultSet.getBoolean("is_admin");
                String password = resultSet.getString("password");
                return new User(id, name, isAdmin, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int getIdFromUser(String name, String password) { //TODO HVIS DER ER EN BEDRE MÅDE SÅ SKAL DET HER VÆK
        try {
            String SQL = "SELECT user_id FROM users WHERE user_name = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void insertUser(String username, String password) {
        try {
            String SQL = "INSERT INTO users (user_name, password) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUserFromName(String name) {
        try {
            String SQL = "SELECT * FROM users WHERE user_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                boolean isAdmin = resultSet.getBoolean("is_admin");
                String password = resultSet.getString("password");
                return new User(id, name, isAdmin, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void assignUserToProject(int userId, int projectId) {
        try {
            String SQL = "INSERT INTO user_project_relation (user_id, project_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, projectId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //bruges til at tjekke om en bruger allerede eksisterer - til når man skal oprette en bruger
    public boolean userAlreadyExists(String username){
        try {
            String SQL = "SELECT * FROM users WHERE user_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void assignUserToTask(int userId, int taskId) {
        try {
            String SQL = "INSERT INTO user_task_relation (user_id, task_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, taskId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> getTasksFromUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        try {
            String SQL = "SELECT t.* FROM tasks t " +
                    "JOIN user_task_relation utr ON t.task_id = utr.task_id " +
                    "WHERE utr.user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("task_id");
                String name = resultSet.getString("task_name");
                String description = resultSet.getString("task_description");
                double hours = resultSet.getDouble("task_hours");
                Date startDate = resultSet.getDate("task_startdate");
                Date deadline = resultSet.getDate("task_deadline");
                Status status = Status.valueOf(resultSet.getString("task_status"));
                tasks.add(new Task(id, name, description, startDate, deadline, hours, status));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    /**TODO**/
    /*public void assignUserToSubproject(int userId, int subprojectId) {
        try {
            String SQL = "INSERT INTO user_subproject_relation (user_id, subproject_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, subprojectId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    /*public List<Subproject> getSubprojectsFromUser(int userId) {
        List<Subproject> subprojects = new ArrayList<>();
        try {
            String SQL = "SELECT s.subproject_id, s.subproject_name, s.subproject_description, s.subproject_hours, s.subproject_startdate, s.subproject_deadline, s.subproject_status " +
                    "FROM subprojects s " +
                    "JOIN user_subproject_relation usr ON s.subproject_id = usr.subproject_id " +
                    "WHERE usr.user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Subproject subproject = mapSubproject(resultSet);
                subprojects.add(subproject);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subprojects;
    }*/
}


