package projectmanament.service;


import org.springframework.beans.factory.annotation.Autowired;
import projectmanament.model.Project;
import org.springframework.stereotype.Service;
import projectmanament.repository.ProjectRepository;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> findAllProjects() {
        return projectRepository.findAllProjects();
    }


}
