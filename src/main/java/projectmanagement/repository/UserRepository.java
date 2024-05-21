package projectmanagement.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import projectmanagement.manager.ConnectionManager;
import projectmanagement.model.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
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

    /** Connection Manager **/
    /** Laver en singleton connection til vores database **/
    @PostConstruct
    public void init() {
        connection = ConnectionManager.getConnection(db_url, username, pwd);
    }

    /** Finder user ved brug af user_id **/
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


    /** Opretter en user ved at indsætte username og password,
     hvorefter databasen selv tildeler useren et unikt user_id**/
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
    /**Finder user_name ud fra informationer om userid, is_admin og password**/
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

    /** Tildeler et projekt til en user, ved at indsætte user_id og project_id i user_project_relation **/
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

    /** Tjekker om en bruger allerede eksisterer - til når man skal oprette en bruger**/
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

    /** Finder tasks som en user er tildelt ved brug af user_task_relation **/
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
}


