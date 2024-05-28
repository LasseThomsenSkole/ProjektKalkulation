package projectmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import projectmanagement.model.Project;
import projectmanagement.model.Subproject;
import projectmanagement.repository.ProjectRepository;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
        List<Subproject> found = repository.getSubprojectsFromProjectId(4);
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
