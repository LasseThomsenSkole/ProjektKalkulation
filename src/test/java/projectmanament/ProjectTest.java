package projectmanament;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import projectmanament.repository.ProjectRepository;

import java.sql.SQLException;

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






}
