package projectmanagement.repository;

import jakarta.annotation.PostConstruct;
import projectmanagement.manager.ConnectionManager;
import projectmanagement.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
public class ProjectRepository {

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

    /** Returner et Project objekt udfra et ResultSet **/
    private Project mapProject(ResultSet rs) throws SQLException {
        int id = rs.getInt("project_id");
        String name = rs.getString("project_name");
        String description = rs.getString("project_description");
        double totalHours = rs.getDouble("total_hours");
        Date startDate = rs.getDate("project_startdate");
        Date deadline = rs.getDate("project_deadline");
        Status status = Status.valueOf(rs.getString("project_status"));

        List<Subproject> subprojects = getSubprojectsFromProjectId(id);
        return new Project(id, name, description, subprojects, totalHours, startDate, deadline, status);
    }
    /** Returner et Subproject objekt udfra et ResultSet **/
    private Subproject mapSubproject(ResultSet rs) throws SQLException {
        int id = rs.getInt("subproject_id");
        String name = rs.getString("subproject_name");
        String description = rs.getString("subproject_description");
        double hours = rs.getDouble("subproject_hours");
        Date startDate = rs.getDate("subproject_startdate");
        Date deadline = rs.getDate("subproject_deadline");
        Status status = Status.valueOf(rs.getString("subproject_status"));

        List<Task> tasks = getTasks(id);
        return new Subproject(id, name, description, hours, startDate, deadline, status, tasks);
    }
    /** Returner et Task objekt udfra et ResultSet **/
    private Task mapTask(ResultSet rs) throws SQLException {
        int id = rs.getInt("task_id");
        String name = rs.getString("task_name");
        String description = rs.getString("task_description");
        double hours = rs.getDouble("task_hours");
        Date startDate = rs.getDate("task_startdate");
        Date deadline = rs.getDate("task_deadline");
        Status status = Status.valueOf(rs.getString("task_status"));

        List<Subtask> subtasks = getSubtasks(id);

        return new Task(id, name, description, startDate, deadline, hours, status, subtasks);
    }
    private Subtask mapSubtask(ResultSet rs) throws SQLException {
        int id = rs.getInt("subtask_id");
        String name = rs.getString("subtask_name");
        String description = rs.getString("subtask_description");
        double hours = rs.getDouble("subtask_hours");
        Date startDate = rs.getDate("subtask_startdate");
        Date deadline = rs.getDate("subtask_deadline");
        Status status = Status.valueOf(rs.getString("subtask_status"));

        return new Subtask(id, name, description, startDate, deadline, hours, status);
    }

    /**GET ALL PROJECTS**/
    /** Den finder informationer fra alle projekter og viser dem.**/
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT project_id, project_name, project_description, total_hours, project_startdate, project_deadline, project_status " +
                "FROM projects;";
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

    /** Kalder på getAllProjects og sorterer dem efter deadline, status eller name**/
    public List<Project> getAllProjectsSorted(String sort) {
        List<Project> projects = getAllProjects();
        switch (sort) {
            case "deadline":
                projects.sort(Comparator.comparing(Project::getDeadline));
                break;
            case "status":
                projects.sort(Comparator.comparing(Project::getStatus));
                break;
            case "name":
            default:
                projects.sort(Comparator.comparing(Project::getName));
                break;
        }
        return projects;
    }

    /**HENT PROJECT**/
    /** Returner et Projekt objekt ved brug af project_id**/
    public Project getProject(int projectId) {
        Project project = null;

        try {
            String SQL = "SELECT project_id, project_name, project_description, total_hours, project_startdate, project_deadline, project_status " +
                    "FROM projects WHERE project_id = ?;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, projectId);
                ResultSet projectResult = preparedStatement.executeQuery();
                if (projectResult.next()) {
                    project = mapProject(projectResult);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return project;
    }

    /**HENT SUBPROJECTS**/
    /** Finder alle info om et subprojekt ved brug af subproject_id**/
    public List<Subproject> getSubprojectsFromProjectId(int projectId) {
        List<Subproject> subprojects = new ArrayList<>();
        try {
            String SQL = "SELECT subproject_id, subproject_name, subproject_description, subproject_hours, subproject_startdate, subproject_deadline, subproject_status " +
                    "FROM subprojects " +
                    "WHERE parent_project_id = ?;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, projectId);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    subprojects.add(mapSubproject(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subprojects;
    }

    /** Henter alle informationer om et subprojekt via subproject_id **/
    public Subproject getSubprojectById(int subprojectId) {
        Subproject subproject = null;
        try {
            String SQL = "SELECT subproject_id, subproject_name, subproject_description, subproject_hours, subproject_startdate, subproject_deadline, subproject_status " +
                    "FROM subprojects " +
                    "WHERE subproject_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, subprojectId);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    subproject = mapSubproject(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subproject;
    }


    /**HENT TASKS**/
    /** Finder alle info om tasks sat til et subprojekt ved brug af subprojects_id**/
    public List<Task> getTasks(int subprojectId){
        List<Task> tasks = new ArrayList<>();
        try {
            String SQL = "SELECT task_id, task_name, task_description, task_hours, task_startdate, task_deadline, task_status " +
                    "FROM tasks " +
                    "WHERE parent_subproject_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, subprojectId);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    tasks.add(mapTask(rs));
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return tasks;
    }

    /** Finder alle info om en task ved brug af task_id**/
    public Task getTaskById(int taskId){
        Task task = null;
        try {
            String SQL = "SELECT task_id, task_name, task_description, task_hours, task_startdate, task_deadline, task_status " +
                    "FROM tasks " +
                    "WHERE task_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, taskId);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    task = mapTask(rs);
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return task;
    }

    /**HENT SUBTASK**/
    /** Finder alle info om subtasks sat til en task ved brug af parent_task_id**/
    public List<Subtask> getSubtasks(int parentTaskId){
        List<Subtask> subtasks = new ArrayList<>();
        try {
            String taskSQL = "SELECT subtask_id, subtask_name, subtask_description, subtask_hours, subtask_startdate, subtask_deadline, subtask_status " +
                    "FROM subtasks " +
                    "WHERE parent_task_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(taskSQL)) {
                preparedStatement.setInt(1, parentTaskId);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    subtasks.add(mapSubtask(rs));
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return subtasks;
    }

    /** Finder alle info om en subtask ved brug af subtask_id**/
    public Subtask getSubtaskById(int subtaskId){
        Subtask subtask = null;
        try {
            String taskSQL = "SELECT subtask_id, subtask_name, subtask_description, subtask_hours, subtask_startdate, subtask_deadline " +
                    "FROM subtasks " +
                    "WHERE subtask_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(taskSQL)) {
                preparedStatement.setInt(1, subtaskId);
                ResultSet subtaskResult = preparedStatement.executeQuery();
                while (subtaskResult.next()) {
                    int id = subtaskResult.getInt("subtask_id");
                    String name = subtaskResult.getString("subtask_name");
                    String description = subtaskResult.getString("subtask_description");
                    double hours = subtaskResult.getDouble("subtask_hours");
                    Date startDate = subtaskResult.getDate("subtask_startdate");
                    Date deadline = subtaskResult.getDate("subtask_deadline");
                    subtask = new Subtask(id, name, description, startDate, deadline, hours);
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return subtask;
    }

    /** Find alle arkiverede projekter ved at finde ud af hvilke projekter har en project_status der er sat til 'ARCHIVED' **/
    public List<Project> findArchivedProjects() {
        List<Project> archivedProjects = new ArrayList<>();
        String query = "SELECT project_id, project_name, project_description, total_hours, project_startdate, project_deadline, project_status " +
                "FROM projects " +
                "WHERE project_status = 'ARCHIVED';";
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
    /** Indsætter informationer ind i project_name, project_description, project_startdate
        og project_deadline for at skabe et nyt projekt **/
    public int createProject(String name, String description, Date startDate, Date deadline) {
        int projectId = 0;
        try {
            String SQL = "INSERT INTO projects (project_name, project_description, project_startdate, project_deadline) " +
                    "VALUES (?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, startDate);
            preparedStatement.setDate(4, deadline);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                projectId = generatedKeys.getInt(1);
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return projectId;
    }

    /**EDIT PROJECT**/
    /** Opdaterer informationer i et projekt ved at give muligheden for at sætte
        en ny value i project_name, project_description, project_startdate og project_deadline via project_id**/
    public void editProject(int projectId,Project updatedProject){
        try {
            String SQL = "UPDATE projects " +
                    "SET project_name = ?, project_description = ?, project_startdate = ?, project_deadline = ? " +
                    "WHERE project_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, updatedProject.getName());
            preparedStatement.setString(2, updatedProject.getDescription());
            preparedStatement.setDate(3, updatedProject.getStartDate());
            preparedStatement.setDate(4, updatedProject.getDeadline());
            preparedStatement.setInt(5,projectId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**SLET PROJECT**/
    /** Sletter alle informationer der har med projektet at gøre inklusivt subprojects,
        tasks og subtasks via parent_project_id. Der er lavet en cascade i db **/
    public void deleteProject(int projectId){
        try {
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
    /** Indsætter informationer ind i subproject_name, subproject_description, subproject_startdate
     og subproject_deadline for at skabe et nyt subprojekt **/
    public int createSubproject(String name, String description, Date startDate, Date deadline, int parentProjectId) {
        int subprojectId = 0;
        try {
            String SQL = "INSERT INTO subprojects (subproject_name, subproject_description, subproject_startdate, subproject_deadline, parent_project_id)" +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, startDate);
            preparedStatement.setDate(4, deadline);
            preparedStatement.setInt(5, parentProjectId);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                subprojectId = generatedKeys.getInt(1);
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return subprojectId;
    }



    /**EDIT SUBPROJECT**/
    /** Opdaterer informationer i et projekt ved at give muligheden for at sætte
     en ny value i subproject_name, subproject_description, subproject_startdate og subproject_deadline via subproject_id**/
    public void editSubproject(int subprojectId, Subproject updatedSubproject) {
        try {
            String SQL = "UPDATE subprojects " +
                            "SET subproject_name = ?, subproject_description = ?, subproject_hours = ?, subproject_startdate = ?, subproject_deadline = ? " +
                            "WHERE subproject_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, updatedSubproject.getName());
            preparedStatement.setString(2, updatedSubproject.getDescription());
            preparedStatement.setDouble(3, updatedSubproject.getHours());
            preparedStatement.setDate(4, updatedSubproject.getStartDate());
            preparedStatement.setDate(5, updatedSubproject.getDeadline());
            preparedStatement.setInt(6, subprojectId);
            preparedStatement.executeUpdate();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /**SLET SUBPROJECT**/
    /** Sletter alle informationer i subprojektet via subprojects_id og fordi vi gør brug af cascading delete i vores sql database**/
    public void deleteSubproject(int subprojectId){
        try {
            String SQL = "DELETE FROM subprojects " +
                    "WHERE subproject_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, subprojectId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    /**OPRET TASK**/
    /** Indsætter informationer ind i task_name, task_description, task_startdate
    og task_deadline for at skabe en ny task**/
    public int createTask(String name, String description, Date startDate, Date deadline, int subprojectId) {
        int taskId = 0;
        try {
            String SQL = "INSERT INTO tasks (task_name, task_description, task_startdate, task_deadline, parent_subproject_id)" +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, startDate);
            preparedStatement.setDate(4, deadline);
            preparedStatement.setInt(5, subprojectId);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                taskId = generatedKeys.getInt(1);
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return taskId;
    }

    /** EDIT TASK **/
    /** Opdaterer informationer i et task ved at give muligheden for at sætte
    en ny value i task_name, task_description, task_startdate og task_deadline via task_id **/
    public void editTask(int taskId, Task edittedTask){
        try{
            String SQL = "UPDATE tasks " +
                    "SET task_name = ?, task_description = ?, task_startdate = ?, task_deadline = ? " +
                    "WHERE task_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, edittedTask.getName());
            preparedStatement.setString(2,edittedTask.getDescription());
            preparedStatement.setDate(3, edittedTask.getStartDate());
            preparedStatement.setDate(4, edittedTask.getDeadline());
            preparedStatement.setInt(5, taskId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**SLET TASK**/
    /** Sletter alle subtasks der har med det ene task at gøre via parent_project_id **/
    public void deleteTask(int taskId){
        try {
            String SQL = "DELETE FROM tasks " +
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
    /** Indsætter informationer ind i subtask_name, subtask_description, subtask_startdate
    og subtask_deadline for at skabe et nyt subtask **/
    public void createSubtask(String name, String description, double hours, Date startDate, Date deadline, int parentTaskId){
        try {
            String SQL = "INSERT INTO subtasks (subtask_name, subtask_description, subtask_hours, subtask_startdate, subtask_deadline, parent_task_id)" +
                    "VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, hours);
            preparedStatement.setDate(4, startDate);
            preparedStatement.setDate(5, deadline);
            preparedStatement.setInt(6, parentTaskId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /** EDIT SUBTASKS **/
    /** Opdaterer informationer i et subtask ved at give muligheden for at sætte
    en ny value i subtask_name, subtask_description, subtask_startdate og subtask_deadline via subtask_id**/
    public void editSubtask(int subtaskId,Subtask edittedSubtask){
        try{
            String SQL = "UPDATE subtasks " +
                    "SET subtask_name = ?, subtask_description = ?,  subtask_startdate = ?, subtask_deadline = ? " +
                    "WHERE subtask_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(5, subtaskId);
            preparedStatement.setString(1, edittedSubtask.getName());
            preparedStatement.setString(2, edittedSubtask.getDescription());
            preparedStatement.setDate(3, edittedSubtask.getStartDate());
            preparedStatement.setDate(4, edittedSubtask.getDeadline());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**SLET SUBTASK**/
    /** Sletter alle informationer i subtask via subtask_id**/
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

    /** Ændrer status for et project **/
    public void changeProjectStatus(int projectID, Status newStatus) {
        String SQL = "UPDATE projects " +
                "SET project_status = ? " +
                "WHERE project_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, newStatus.name());
            preparedStatement.setInt(2, projectID);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalStateException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Ændrer status for et subproject **/
    public void changeSubprojectStatus(int subprojectID, Status newStatus){
        try{
            String SQL = "UPDATE subprojects " +
                    "SET subproject_status = ? " +
                    "WHERE subproject_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, newStatus.name()); //jdbc benytter sig ikke af enums, så vi skal bruge .name(). lasse
            preparedStatement.setInt(2, subprojectID);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalStateException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Ændrer status for en task **/
    public void changeTaskStatus(int taskID, Status newStatus){
        try{
            String SQL = "UPDATE tasks " +
                    "SET task_status = ? " +
                    "WHERE task_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, newStatus.name());
            preparedStatement.setInt(2, taskID);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalStateException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Ændrer status for en subtask **/
    public void changeSubtaskStatus(int subtaskID, Status newStatus){
        try{
            String SQL = "UPDATE subtasks " +
                    "SET subtask_status = ? " +
                    "WHERE subtask_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, newStatus.name());
            preparedStatement.setInt(2, subtaskID);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalStateException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Tilføjer projekter ejer af user, til users 'min side' **/
    public List<Project> getProjectsFromAssignedUser(int userId) {
        List<Project> projects = new ArrayList<>();
        try {
            String SQL = "SELECT p.project_id, p.project_name, p.project_description, p.total_hours, p.project_startdate, p.project_deadline, p.project_status " +
                    "FROM projects p " +
                    "JOIN user_project_relation upr ON p.project_id = upr.project_id " +
                    "WHERE upr.user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                projects.add(mapProject(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return projects;
    }

    /**Benyttes for at redirecte fra task status ændring til parent subproject side**/
    public int findSubprojectIdByTaskId(int taskId) {
        int subprojectId = 0;
        try {
            String SQL = "SELECT parent_subproject_id " +
                    "FROM tasks " +
                    "WHERE task_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, taskId);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    subprojectId = rs.getInt("parent_subproject_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subprojectId;
    }

    public int findTaskIdBySubtaskId(int subtaskId) {
        int taskId = 0;
        try {
            String SQL = "SELECT parent_task_id " +
                    "FROM subtasks " +
                    "WHERE subtask_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, subtaskId);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    taskId = rs.getInt("parent_task_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return taskId;
    }

    public int findParentProjectIdBySubprojectId(int subprojectId) {
        int projectId = 0;
        try {
            String SQL = "SELECT parent_project_id FROM subprojects WHERE subproject_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setInt(1, subprojectId);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    projectId = rs.getInt("parent_project_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return projectId;
    }
}