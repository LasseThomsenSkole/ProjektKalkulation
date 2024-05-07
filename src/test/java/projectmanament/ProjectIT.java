package projectmanament;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import projectmanament.manager.ConnectionManager;
import projectmanament.model.IncorrectProjectIDException;
import projectmanament.model.Project;
import projectmanament.repository.ProjectRepository;

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

            String initSchema = "CREATE SCHEMA IF NOT EXISTS AlphaManagement";
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
            String createTable = "CREATE TABLE projects (" +
                "project_id INTEGER PRIMARY KEY, " +
                "project_name VARCHAR(30), " +
                "project_description VARCHAR(30), " +
                "total_hours INTEGER, " +
                "project_deadline DATE" +
                ");";
        stmt.addBatch(createTable);
            stmt.executeBatch();
            System.out.println("Database created");

            String sqlInsertRow = "INSERT INTO projects VALUES (10,'En test','Test', 11, '2024-11-01')";
            stmt.addBatch(sqlInsertRow);
            sqlInsertRow = "INSERT INTO projects VALUES (20,'To test','Test test', 21, '2024-11-02')";
            stmt.addBatch(sqlInsertRow);
            sqlInsertRow = "INSERT INTO projects VALUES (30,'Tre test','Test test test', 31, '2024-11-03')";
            stmt.addBatch(sqlInsertRow);
            sqlInsertRow = "INSERT INTO projects VALUES(40,'Fire test','Test test test test', 41, '2024-11-04')";
            stmt.addBatch(sqlInsertRow);
            int rows[] = stmt.executeBatch();
            System.out.println("Inserted " + rows.length + " records into the table");

    }

    @Test
    void findProjectByID() throws SQLException {
        Project found = repository.getProject(10);
        assertEquals("En test", found.getName());
    }

    @Test
    void deleteProject20() throws IncorrectProjectIDException {
        repository.deleteProject(20);
    }

    @Test
    void findDepartment40() throws IncorrectProjectIDException, SQLException {
        Project found = repository.getProject(30);
        assertNotNull(found);
    }


}