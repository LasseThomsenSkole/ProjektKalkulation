package projectmanagement;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import projectmanagement.manager.ConnectionManager;
import projectmanagement.model.Project;
import projectmanagement.model.User;
import projectmanagement.repository.ProjectRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class) // extender JUnit
@SpringJUnitConfig
public class ProjectIT {

    @Autowired
    private ProjectRepository repository;

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

    /** Setup af Database **/
    @BeforeEach
    public void setup() throws SQLException {

            Statement stmt = connection.createStatement();

            String initSchema = "CREATE SCHEMA IF NOT EXISTS AlphaManagementTestDB";
            String dropTableUsers = "drop table if exists users";
            String dropTableProjects = "drop table if exists projects";
            String dropTableSubProjects = "drop table if exists subprojects";
            String dropTableTasks = "drop table if exists tasks";
            String dropTableSubTasks = "drop table if exists subtasks";
            String dropTableUserProjectRelation = "drop table if exists user_project_relation";
            stmt.addBatch(initSchema);
            stmt.addBatch(dropTableUsers);
            stmt.addBatch(dropTableProjects);
            stmt.addBatch(dropTableSubProjects);
            stmt.addBatch(dropTableTasks);
            stmt.addBatch(dropTableSubTasks);
            stmt.addBatch(dropTableUserProjectRelation);
            String createUserTable = "CREATE TABLE IF NOT EXISTS users(" +
                    "    user_id int auto_increment primary key," +
                    "    user_name varchar(50)," +
                    "    is_admin BOOL not null default false," +
                    "    password varchar(50)" +
                    "    );";
            stmt.addBatch(createUserTable);
            String createProjectTable = "CREATE TABLE projects (" +
                "project_id INTEGER PRIMARY KEY, " +
                "project_name VARCHAR(30), " +
                "project_description VARCHAR(30), " +
                "total_hours INTEGER, " +
                "project_deadline DATE," +
                    "project_status ENUM('DONE', 'IN_PROGRESS','TODO','ARCHIVED', 'NOT_STARTED') not null default 'NOT_STARTED'" +
                ");";
            stmt.addBatch(createProjectTable);
            String createSubprojectTable = "CREATE TABLE subprojects(" +
                    "subproject_id int auto_increment primary key," +
                    "    subproject_name varchar(50) not null," +
                    "    subproject_description varchar(500) not null," +
                    "    subproject_hours double not null default 0," +
                    "    subproject_deadline date," +
                    "    subproject_status enum('DONE', 'IN_PROGRESS','TODO','ARCHIVED','NOT_STARTED') not null default 'NOT_STARTED'," +
                    "    parent_project_id int not null" +
                    "    );";
            stmt.addBatch(createSubprojectTable);
            String createTaskTable = "CREATE TABLE tasks (" +
                    "    task_id int auto_increment primary key," +
                    "    task_name varchar(50) not null," +
                    "    task_description varchar(500) not null," +
                    "    task_hours double not null default 0," +
                    "    task_deadline date," +
                    "    task_status enum('DONE', 'IN_PROGRESS','TODO','ARCHIVED','NOT_STARTED') not null default 'NOT_STARTED'," +
                    "    subproject_id int not null" +
                    "    );";
            stmt.addBatch(createTaskTable);
            String createSubtaskTable = "CREATE TABLE subtasks(" +
                    "    subtask_id int auto_increment primary key," +
                    "    subtask_name varchar(50) not null," +
                    "    subtask_description varchar(500) not null," +
                    "    subtask_hours double not null default 0," +
                    "    subtask_deadline date," +
                    "    subtask_status enum('DONE', 'IN_PROGRESS','TODO','ARCHIVED', 'NOT_STARTED') not null default 'NOT_STARTED'," +
                    "    parent_task_id int not null" +
                    "    );";
            stmt.addBatch(createSubtaskTable);
            stmt.executeBatch();
            System.out.println("Database created");

            String sqlInsertRow = "INSERT INTO users VALUES (12,'Adam', 0, 'test')";
            stmt.addBatch(sqlInsertRow);
            sqlInsertRow = "INSERT INTO users VALUES (13, 'admin', 1, 'kage')";
            stmt.addBatch(sqlInsertRow);
            sqlInsertRow = "INSERT INTO users VALUES (14, 'Brian', 0, 'Fest')";
            stmt.addBatch(sqlInsertRow);
            sqlInsertRow = "INSERT INTO projects VALUES (10,'test','Test', 11, '2024-11-01', 'NOT_STARTED')";
            stmt.addBatch(sqlInsertRow);
            sqlInsertRow = "INSERT INTO projects VALUES (20,'To test','Test test', 21, '2024-11-02', 'IN_PROGRESS')";
            stmt.addBatch(sqlInsertRow);
            sqlInsertRow = "INSERT INTO projects VALUES (30,'Tre test','Test test test', 31, '2024-11-03', 'DONE')";
            stmt.addBatch(sqlInsertRow);
            sqlInsertRow = "INSERT INTO projects VALUES(40,'Fire test','Test test test test', 41, '2024-11-04', 'DONE')";
            stmt.addBatch(sqlInsertRow);
            int rows[] = stmt.executeBatch();
            System.out.println("Inserted " + rows.length + " records into the table");
    }

//    @Test
//    void findUserbyID() throws SQLException {
//        User found = repository.getUserById(12);
//        assertEquals("Adam", found.getName());
//    }

    @Test
    void deleteProject20() {
        repository.deleteProject(20);
    }

    @Test
    void findProject30() throws SQLException {
        Project found = repository.getProject(30);
        assertNotNull(found);
    }


}