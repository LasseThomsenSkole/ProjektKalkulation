package projectmanament.service;


import projectmanament.model.Project;
import org.springframework.stereotype.Service;
import projectmanament.repository.ProjectRepository;

import java.util.List;

@Service
public class ProjectService {
    private ProjectRepository projectRepository;

    public List<Project> findAllProjects() {
        return projectRepository.findAllProjects();
    }


}
