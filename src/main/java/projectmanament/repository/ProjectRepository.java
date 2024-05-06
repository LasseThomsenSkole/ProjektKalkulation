package projectmanament.repository;

import jakarta.annotation.PostConstruct;
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

    public void test() {
        System.out.println(username);
    }

    /**
     * GET ALL PROJECTS
     **/
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


    /**
     * HENT PROJECT
     **/
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


    /**
     * HENT SUBPROJECT
     **/
    public List<Subproject> getSubprojects(int subprojectId) {
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
                    Status status = Status.valueOf(subprojectResult.getString("subproject_status"));
                    subprojects.add(new Subproject(id, name, description, hours, deadline, status));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subprojects;
    }


    /**
     * HENT TASK
     **/
    public List<Task> getTasks(int taskId) { //TODO lav om så den finder tasks ud fra taskID ??? -lasse
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }


    /**
     * HENT SUBTASK
     **/
    public List<Subtask> getSubtasks(int subtaskId) {//TODO lav om så den finder tasks ud fra subtaskID ??? -lasse
        List<Subtask> subtasks = new ArrayList<>();
        try {
            String taskSQL = "SELECT subtask_id, subtask_name, subtask_description, subtask_hours, subtask_deadline " +
                    "FROM subtasks " +
                    "WHERE parent_task_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(taskSQL);
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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subtasks;
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
                    "total_hours, project_deadline) VALUES (?, ?, 0, ?);"; // sets total hours to zero
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, deadline);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
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
            preparedStatement.setInt(4, projectId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /**SLET PROJECT**/
    public void deleteProject(int projectId){
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
        } catch (SQLException e) {
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
        } catch (SQLException e) {
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /**SLET SUBPROJECT**/
    public void deleteSubproject(int subprojectId){
        try {
            String SQL = "DELETE FROM subprojects " +
                    "WHERE subprojects_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, subprojectId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
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
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count == 0; // return true hvis det er ikke er noget
            }
        } catch (SQLException e) {
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
        } catch (SQLException e) {
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
            preparedStatement.setString(2, edittedTask.getDescription());
            preparedStatement.setDate(3, new Date(edittedTask.getDeadline().getTime())); //java date => SQL date
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /**SLET TASK**/
    public void deleteTask(int taskId){
        try {
            String SQL = "DELETE FROM tasks " +
                    "WHERE task_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, taskId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
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
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Project> findAllProjectsByStatus(Status status) {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT project_id, project_name, project_description, total_hours, project_deadline, project_status FROM projects WHERE project_status = ?;";
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
    /** change status for project**/
    public void changeProjectStatus(int projectID, Status newStatus){
        try{
            String SQL = "UPDATE projects SET project_status = ? WHERE project_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, newStatus.name()); //jdbc benytter sig ik a enums så vi skal bruge .name()
            preparedStatement.setInt(2, projectID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /** change status for subproject **/
    public void changeSubprojectStatus(int subprojectID, Status newStatus){
        try{
            String SQL = "UPDATE subprojects SET subproject_status = ? WHERE subproject_id = ?";
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
            String SQL = "UPDATE tasks SET task_status = ? WHERE task_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, newStatus.name()); //jdbc benytter sig ik a enums så vi skal bruge .name()
            preparedStatement.setInt(2, taskID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /** change subtask status **/
    public void changeSubtaskStatus(int subtaskID, Status newStatus){
        try{
            String SQL = "UPDATE subtasks SET subtask_status = ? WHERE subtask_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, newStatus.name()); //jdbc benytter sig ik a enums så vi skal bruge .name()
            preparedStatement.setInt(2, subtaskID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
