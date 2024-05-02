package projectmanament.controller;

import org.springframework.beans.factory.annotation.Value;
import projectmanament.model.Project;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import projectmanament.service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("")
public class ProjectController {
    private ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @GetMapping("")
    public String index(){
        System.out.println("asdasdasd");
        return "index";
    }

    @GetMapping("/teamprojects")
    public String showProjects(Model model) {
        List<Project> projects = projectService.findAllProjects();
        model.addAttribute("projects", projects);
        return "projects";
    }
}
