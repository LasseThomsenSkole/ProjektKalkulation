package projectmanament;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import projectmanament.model.Project;
import projectmanament.model.Subproject;
import projectmanament.repository.ProjectRepository;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
public class ProjectTest {

    @Autowired
    ProjectRepository repository;

    @Test
    void testSingletonPerformance() throws SQLException {
        for (int i = 0; i < 1000; i++) {
            repository.getProject(1);
        }
    }

    @Test
    void findProject() throws SQLException {
        Project found = repository.getProject(1);
        assertEquals("Project Alpha", found.getName());
    }

    @Test
    void deleteProject3()  {
        repository.deleteProject(3);
    }

    @Test
    void findSubProject() throws SQLException {
        List<Subproject> found = repository.getSubprojects(4);
        assertNotNull(found);
    }
/*
    @Test
    void changeProjectStatus() throws SQLException {
        boolean status = repository.changeProjectStatus();
        assertTrue(status);
    }

   /* @Test
    void addProject() throws SQLException {
        Project project = repository.createProject(new Project(10,"TEST", "test", 11, 2024-06-02));
        assertTrue(project.getId()> 10);
    }
*/



}
