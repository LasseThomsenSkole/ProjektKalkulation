package projectmanament.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import projectmanament.model.Project;
import org.springframework.stereotype.Controller;
import projectmanament.model.Status;
import projectmanament.service.ProjectService;
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

    @GetMapping("/project/{id}")
    public String showProjectDetails(@PathVariable int id, Model model) {
        Project project = projectService.getProject(id);
        model.addAttribute("project", project);
        return "project-detail";
    }

    @GetMapping("/projects")
    public String showProjectsByStatus(@RequestParam(required = false) Status status, Model model) {
        List<Project> projects;
        if (status != null) {
            projects = projectService.findAllProjectsByStatus(status);
        } else {
            projects = projectService.findAllProjects();
        }
        model.addAttribute("projects", projects);
        return "projects"; // View name
    }

    @GetMapping("/archivedprojects")
    public String showArchivedProjects(Model model) {
        List<Project> archivedProjects = projectService.findArchivedProjects();
        model.addAttribute("archivedProjects", archivedProjects);
        return "archived-projects";
    }
}
