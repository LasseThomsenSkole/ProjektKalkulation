package controller;

import model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("")
public class ProjectController {

    private ProjectService projectService;

    @GetMapping("")
    public String index(){
        return "index";
    }

    @GetMapping("/teamprojects")
    public String showProjects(Model model) {
        List<Project> projects = projectService.findAllProjects();
        model.addAttribute("projects", projects);
        return "projects";
    }
}
