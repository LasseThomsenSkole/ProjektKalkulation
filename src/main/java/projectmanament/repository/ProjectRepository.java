package projectmanament.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import projectmanament.manager.ConnectionManager;
import projectmanament.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//import static com.sun.beans.introspect.PropertyInfo.Name.description;

@Repository
public class ProjectRepository {

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
    public User getUserById(int userId){
        try{
            String SQL = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int id = resultSet.getInt("user_id");
                String name = resultSet.getString("user_name");
                boolean isAdmin = resultSet.getBoolean("is_admin");
                String password = resultSet.getString("password");
                return new User(id, name, isAdmin, password);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }
    public int getIdFromUser(String name, String password){ //TODO HVIS DER ER EN BEDRE MÅDE SÅ SKAL DET HER VÆK
        try{
            String SQL = "SELECT user_id FROM users WHERE user_name = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return 0;
    }
    public void insertUser(String username, String password){
        try {
            String SQL = "INSERT INTO users (user_name, password) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**GET ALL PROJECTS**/
    public List<Project> findAllProjects() {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT project_id, project_name, project_description, total_hours, project_deadline, project_status FROM projects;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                projects.add(mapProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all projects", e);
        }
        return projects;
    }

    private Project mapProject(ResultSet rs) throws SQLException {
        int id = rs.getInt("project_id");
        String name = rs.getString("project_name");
        String description = rs.getString("project_description");
        double totalHours = rs.getDouble("total_hours");
        Date deadline = rs.getDate("project_deadline");
        Status status = Status.valueOf(rs.getString("project_status")); // Assuming you have added a 'status' column to your projects table

        List<Task> tasks = getTasks(id);
        List<Subproject> subprojects = getSubprojects(id);
        return new Project(id, name, description, tasks, subprojects, totalHours, deadline, status);
    }


    /**HENT PROJECT**/
    public Project getProject(int projectId) { //gets project + subproject and tasks
        Project project = null;

        try {
            String SQL = "SELECT project_id, project_name, project_description, total_hours, project_deadline, project_status " +
                    "FROM projects WHERE project_id = ?;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, projectId);
                ResultSet projectResult = preparedStatement.executeQuery();
                if (projectResult.next()) {
                    int id = projectResult.getInt("project_id");
                    String name = projectResult.getString("project_name");
                    String description = projectResult.getString("project_description");
                    double totalHours = projectResult.getDouble("total_hours");
                    Date deadline = projectResult.getDate("project_deadline");
                    Status status = Status.valueOf(projectResult.getString("project_status"));

                    // Fetches tasks and subprojects for a particular project
                    List<Task> tasks = getTasks(id);
                    List<Subproject> subprojects = getSubprojects(id);

                    // Creates project with all details
                    project = new Project(id, name, description, tasks, subprojects, totalHours, deadline, status);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return project;
    }



    /**HENT SUBPROJECTS**/
    public List<Subproject> getSubprojects(int projectId) {
        List<Subproject> subprojects = new ArrayList<>();
        try {
            String SQL = "SELECT subproject_id, subproject_name, subproject_description, subproject_hours, subproject_deadline, subproject_status " +
                    "FROM subprojects " +
                    "WHERE parent_project_id = ?;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, projectId);
                ResultSet subprojectResult = preparedStatement.executeQuery();
                while (subprojectResult.next()) {
                    int id = subprojectResult.getInt("subproject_id");
                    String name = subprojectResult.getString("subproject_name");
                    String description = subprojectResult.getString("subproject_description");
                    double hours = subprojectResult.getDouble("subproject_hours");
                    Date deadline = subprojectResult.getDate("subproject_deadline");
                    Status status = Status.valueOf(subprojectResult.getString("subproject_status"));
                    subprojects.add(new Subproject(id, name, description, hours, deadline, status));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subprojects;
    }

    //til når vi skal edit ét subproject
    public Subproject getSubprojectById(int subprojectId) {
        Subproject subproject = null;
        try {
            String SQL = "SELECT subproject_id, subproject_name, subproject_description, subproject_hours, subproject_deadline, subproject_status " +
                    "FROM subprojects " +
                    "WHERE subproject_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, subprojectId);
                ResultSet subprojectResult = preparedStatement.executeQuery();
                if (subprojectResult.next()) {
                    int id = subprojectResult.getInt("subproject_id");
                    String name = subprojectResult.getString("subproject_name");
                    String description = subprojectResult.getString("subproject_description");
                    double hours = subprojectResult.getDouble("subproject_hours");
                    Date deadline = subprojectResult.getDate("subproject_deadline");
                    Status status = Status.valueOf(subprojectResult.getString("subproject_status"));
                    List<Task> tasks = getTasks(id);  // get tasks for particular subproject
                    subproject = new Subproject(id, name, description, hours, deadline, status, tasks);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subproject;
    }


    /**HENT TASKS**/
    public List<Task> getTasks(int subprojectId){
        List<Task> tasks = new ArrayList<>();
        try {
            String SQL = "SELECT task_id, task_name, task_description, task_hours, task_deadline, task_status " +
                    "FROM tasks " +
                    "WHERE subproject_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, subprojectId);
                ResultSet taskResult = preparedStatement.executeQuery();
                while (taskResult.next()) {
                    int id = taskResult.getInt("task_id");
                    String name = taskResult.getString("task_name");
                    String description = taskResult.getString("task_description");
                    double hours = taskResult.getDouble("task_hours");
                    Date deadline = taskResult.getDate("task_deadline");
                    Status status = Status.valueOf(taskResult.getString("task_status"));
                    tasks.add(new Task(id, name, description, deadline, hours, status));
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return tasks;
    }

    //hent kun én task
    public Task getTaskById(int taskId){
        Task task = null;
        try {
            String SQL = "SELECT task_id, task_name, task_description, task_hours, task_deadline, task_status " +
                    "FROM tasks " +
                    "WHERE task_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, taskId);
                ResultSet taskResult = preparedStatement.executeQuery();
                while (taskResult.next()) {
                    int id = taskResult.getInt("task_id");
                    String name = taskResult.getString("task_name");
                    String description = taskResult.getString("task_description");
                    double hours = taskResult.getDouble("task_hours");
                    Date deadline = taskResult.getDate("task_deadline");
                    Status status = Status.valueOf(taskResult.getString("task_status"));
                    task = new Task(id, name, description, deadline, hours, status);
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return task;
    }

    /**HENT SUBTASK**/
    public List<Subtask> getSubtasks(int parentTaskId){
        List<Subtask> subtasks = new ArrayList<>();
        try {
            String taskSQL = "SELECT subtask_id, subtask_name, subtask_description, subtask_hours, subtask_deadline, subtask_status " +
                    "FROM subtasks " +
                    "WHERE parent_task_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(taskSQL)) {
                preparedStatement.setInt(1, parentTaskId);
                ResultSet taskResult = preparedStatement.executeQuery();
                while (taskResult.next()) {
                    int id = taskResult.getInt("subtask_id");
                    String name = taskResult.getString("subtask_name");
                    String description = taskResult.getString("subtask_description");
                    double hours = taskResult.getDouble("subtask_hours");
                    Date deadline = taskResult.getDate("subtask_deadline");
                    Status status = Status.valueOf(taskResult.getString("subtask_status"));
                    subtasks.add(new Subtask(id, name, description, deadline, hours, status));
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return subtasks;
    }

    //hent kun én subtask
    public Subtask getSubtaskById(int subtaskId){
        Subtask subtask = null;
        try {
            String taskSQL = "SELECT subtask_id, subtask_name, subtask_description, subtask_hours, subtask_deadline " +
                    "FROM subtasks " +
                    "WHERE subtask_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(taskSQL)) {
                preparedStatement.setInt(1, subtaskId);
                ResultSet taskResult = preparedStatement.executeQuery();
                while (taskResult.next()) {
                    int id = taskResult.getInt("subtask_id");
                    String name = taskResult.getString("subtask_name");
                    String description = taskResult.getString("subtask_description");
                    double hours = taskResult.getDouble("subtask_hours");
                    Date deadline = taskResult.getDate("subtask_deadline");
                    subtask = new Subtask(id, name, description, deadline, hours);
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return subtask;
    }




    public List<Project> findArchivedProjects() {
        List<Project> archivedProjects = new ArrayList<>();
        String query = "SELECT project_id, project_name, project_description, total_hours, project_deadline, project_status FROM projects WHERE project_status = 'ARCHIVED';";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                archivedProjects.add(mapProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding archived projects", e);
        }
        return archivedProjects;
    }




    /**OPRET PROJECT**/
    public void createProject(String name, String description, Date deadline) {
        try {
            String SQL = "INSERT INTO projects (project_name, project_description, " +
                    "project_deadline) VALUES (?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, deadline);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**EDIT PROJECT**/
    public void editProject(int projectId,Project updatedProject){
        try {
            String SQL = "UPDATE projects " +
                    "SET project_name = ?, project_description = ?, project_deadline = ? " +
                    "WHERE project_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, updatedProject.getName());
            preparedStatement.setString(2, updatedProject.getDescription());
            preparedStatement.setDate(3, updatedProject.getDeadline());
            preparedStatement.setInt(4,projectId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**SLET PROJECT**/
    public void deleteProject(int projectId){ //todo måske lav til sql trigger
        try {
            //sletter subprojects fra projects
            String deleteSubprojects = "DELETE FROM subprojects " +
                    "WHERE parent_project_id = ?";
            PreparedStatement deleteSubprojectsStatement = connection.prepareStatement(deleteSubprojects);
            deleteSubprojectsStatement.setInt(1, projectId);
            deleteSubprojectsStatement.executeUpdate();

            //sletter tasks fra subprojects
            String deleteTasks = "DELETE FROM tasks " +
                    "WHERE subproject_id " +
                    "IN (SELECT subproject_id FROM subprojects WHERE parent_project_id = ?)";
            PreparedStatement deleteTasksStatement = connection.prepareStatement(deleteTasks);
            deleteTasksStatement.setInt(1, projectId);
            deleteTasksStatement.executeUpdate();

            //sletter subtasks fra tasks
            String deleteSubtasks = "DELETE FROM subtasks " +
                    "WHERE parent_task_id " +
                    "IN (SELECT task_id FROM tasks WHERE subproject_id IN (SELECT subproject_id FROM subprojects WHERE parent_project_id = ?))";
            PreparedStatement deleteSubtasksStatement = connection.prepareStatement(deleteSubtasks);
            deleteSubtasksStatement.setInt(1, projectId);
            deleteSubtasksStatement.executeUpdate();

            //sletter selve projectet
            String SQL = "DELETE FROM projects " +
                    "WHERE project_id = ?";
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



    /**EDIT SUBPROJECT**/
    public void editSubproject(int subprojectId, Subproject updatedSubproject) {
        try {
            String SQL = "UPDATE subprojects " +
                            "SET subproject_name = ?, subproject_description = ?, subproject_hours = ?, subproject_deadline = ? " +
                            "WHERE subproject_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, updatedSubproject.getName());
            preparedStatement.setString(2, updatedSubproject.getDescription());
            preparedStatement.setDouble(3, updatedSubproject.getHours());
            preparedStatement.setDate(4, updatedSubproject.getDeadline());
            preparedStatement.setInt(5, subprojectId);
            preparedStatement.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /**SLET SUBPROJECT**/
    public void deleteSubproject(int subprojectId){ //todo den burde også slette tasks og subtasks som ligger under
        try {
            String SQL = "DELETE FROM subprojects " +
                    "WHERE subprojects_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, subprojectId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }



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
    /** EDIT TASK **/
    public void editTask(int taskId, Task edittedTask){
        try{
            String SQL = "UPDATE tasks " +
                    "SET task_name = ?, task_description = ?, task_deadline = ? " +
                    "WHERE task_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(4, taskId);
            preparedStatement.setString(1, edittedTask.getName());
            preparedStatement.setString(2,edittedTask.getDescription());
            preparedStatement.setDate(3, new Date(edittedTask.getDeadline().getTime())); //java date => SQL date
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**SLET TASK**/
    public void deleteTask(int taskId){ //todo preparedStatement skal måske close ????
        try {
            String deleteSubtaskSQL = "DELETE FROM subtasks WHERE parent_task_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSubtaskSQL);
            preparedStatement.setInt(1, taskId);
            preparedStatement.executeUpdate();

            String SQL = "DELETE FROM tasks " +
                    "WHERE task_id = ?;";
            preparedStatement = connection.prepareStatement(SQL);
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
    /** EDIT SUBTASKS **/
    public void editSubtask(int subtaskId,Subtask edittedSubtask){
        try{
            String SQL = "UPDATE subtasks " +
                    "SET subtask_name = ?, subtask_description = ?, subtask_deadline = ? " +
                    "WHERE subtask_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(4, subtaskId);
            preparedStatement.setString(1, edittedSubtask.getName());
            preparedStatement.setString(2, edittedSubtask.getDescription());
            preparedStatement.setDate(3, new Date(edittedSubtask.getDeadline().getTime())); //java date => SQL date
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**SLET SUBTASK**/
    public void deleteSubtask(int subtaskId){
        try {
            String SQL = "DELETE FROM subtasks " +
                    "WHERE subtask_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, subtaskId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    public List<Project> findAllProjectsByStatus(Status status) {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT project_id, project_name, project_description, total_hours, project_deadline, project_status " +
                "FROM projects " +
                "WHERE project_status = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, status.toString());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapProject(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding projects by status", e);
        }
        return projects;
    }
    /** Change status for project **/
    public void changeProjectStatus(int projectID, Status newStatus) {
        String SQL = "UPDATE projects " +
                "SET project_status = ? " +
                "WHERE project_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, newStatus.name());
            preparedStatement.setInt(2, projectID);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalStateException("No project found with ID: " + projectID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project status", e);
        }
    }
    /** change status for subproject **/
    public void changeSubprojectStatus(int subprojectID, Status newStatus){
        try{
            String SQL = "UPDATE subprojects " +
                    "SET subproject_status = ? " +
                    "WHERE subproject_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, newStatus.name()); //jdbc benytter sig ik a enums så vi skal bruge .name()
            preparedStatement.setInt(2, subprojectID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** change status for task **/
    public void changeTaskStatus(int taskID, Status newStatus){
        try{
            String SQL = "UPDATE tasks " +
                    "SET task_status = ? " +
                    "WHERE task_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, newStatus.name()); //jdbc benytter sig ik af enums så vi skal bruge .name()
            preparedStatement.setInt(2, taskID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** change subtask status **/
    public void changeSubtaskStatus(int subtaskID, Status newStatus){
        try{
            String SQL = "UPDATE subtasks " +
                    "SET subtask_status = ? " +
                    "WHERE subtask_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, newStatus.name()); //jdbc benytter sig ik af enums så vi skal bruge .name()
            preparedStatement.setInt(2, subtaskID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Project> getProjectsFromUser(int userId){
        List<Project> projects = new ArrayList<>();
        try {
            String SQL = "SELECT project_id, project_name, project_description, total_hours, project_deadline, project_status " +
                    "FROM projects " +
                    "JOIN user_project_relation " +
                    "ON project.project_id = user_project_relation.project_id " +
                    "WHERE user_project_relation.user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("project_id");
                String name = resultSet.getString("project_name");
                String description = resultSet.getString("project_description");
                double totalHours = resultSet.getDouble("total_hours");
                Date deadline = resultSet.getDate("project_deadline");
                Status status = Status.valueOf(resultSet.getString("project_status"));

                List<Task> tasks = getTasks(id);
                List<Subproject> subprojects = getSubprojects(id);
                Project project = new Project(id, name,description, tasks, subprojects, totalHours, deadline, status);
                projects.add(project);
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return projects;
    }





}
