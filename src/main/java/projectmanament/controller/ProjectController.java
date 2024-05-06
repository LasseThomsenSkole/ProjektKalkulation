package projectmanament.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projectmanament.model.Project;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import projectmanament.service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.sql.Date;
import java.time.LocalDate;
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

    @GetMapping("/teamprojects/create")
    public String createProjectForm() {
        return "create-project";
    }

    @PostMapping("/teamprojects/create")
    public String createProject(@RequestParam String name, @RequestParam String description, @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline, Model model) {
        projectService.createProject(name, description, Date.valueOf(deadline));
        return "redirect:/teamprojects";
    }
}
