package projectmanament.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import projectmanament.model.*;
import org.springframework.stereotype.Controller;
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

    private boolean isLoggedIn(HttpSession session){
        return session.getAttribute("user") != null;
    }

    @GetMapping("")
    public String index(HttpSession session, Model model){
        if (isLoggedIn(session)){
            model.addAttribute("user", session.getAttribute("user"));
            return "index";
        }
        return "login";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    public String login(
                        @RequestParam("username") String name,
                        @RequestParam("password") String password,
                        HttpSession session, Model model){
        if (projectService.login(name,password)){
            session.setAttribute("user", projectService.getUserFromName(name)); //todo spørg om der er en bedre måde at gøre det her på
            session.setMaxInactiveInterval(300); //30 sekunder
            return "redirect:/";
        }
        model.addAttribute("wrongCredentials", true); //det her kan vi tjekke for i html !!!!
        return "login";
    }

    @GetMapping("/createaccount")
    public String createAccount(HttpSession session){
        if (isLoggedIn(session)){
            User user = (User) session.getAttribute("user");
            return user.isAdmin() ? "create-account" : "login"; //hvis man er admin kan man oprette accounts
        }
        return "login";
    }

    @PostMapping("/createaccount")
    public String createAccountPost(@RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    HttpSession session){
        if (isLoggedIn(session)){
            User user = (User) session.getAttribute("user");
            if (user.isAdmin()){
                projectService.insertUser(username, password);
                return "redirect:/login"; //TODO måske lave en account endpoint???? -Lasse
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String LogOut(HttpSession session){
        session.invalidate();
        return "login";
    }

    @GetMapping("/projects")
    public String showAllProjects(@RequestParam(value = "sort", required = false) String sort, Model model, HttpSession session) {
        List<Project> projects;
        if (sort != null) {
            projects = projectService.findAllProjectsSorted(sort);
        } else {
            projects = projectService.findAllProjects();
        }
        model.addAttribute("projects", projects);
        return isLoggedIn(session) ? "projects" : "login"; //hvis isLoggedIn returns false så return til login
    }

    @GetMapping("/projects/create")
    public String createProjectForm(HttpSession session) {
        if (!isLoggedIn(session)){
            return "login";
        }
        return "create-project";
    }

    @PostMapping("/projects/create")
    public String createProject(@RequestParam String name, @RequestParam String description, @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                                @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, HttpSession session) { //todo måske skal det være et Project objekt som parameter - lasse
        if (isLoggedIn(session)){
            User user = (User) session.getAttribute("user");

            int projectId = projectService.createProject(name, description, Date.valueOf(startDate), Date.valueOf(deadline));
            projectService.createProjectRelation(user.getId(),projectId);
        }

        return "redirect:/projects";
    }

    @GetMapping("/project/{id}")
    public String showProjectDetails(@PathVariable int id, Model model) {
        Project project = projectService.getProject(id);
        model.addAttribute("project", project);
        return "project-detail";
    }

    @GetMapping("/archivedprojects")
    public String showArchivedProjects(Model model) {
        List<Project> archivedProjects = projectService.findArchivedProjects();
        model.addAttribute("archivedProjects", archivedProjects);
        return "archived-projects";
    }

    @PostMapping("/projects/{projectId}/status")
    public String updateProjectStatus(@PathVariable("projectId") int projectId, @RequestParam("newStatus") String statusString) {
        Status newStatus = Status.valueOf(statusString);
        projectService.changeProjectStatus(projectId, newStatus);
        return "redirect:/projects";
    }

    @PostMapping("/subprojects/{subprojectId}/status")
    public String updateSubprojectStatus(@PathVariable("subprojectId") int subprojectId,
                                         @RequestParam("newStatus") String statusString) {
        Status newStatus = Status.valueOf(statusString);
        projectService.changeSubprojectStatus(subprojectId, newStatus);
        return "redirect:/subproject/" + subprojectId;
    }

    @PostMapping("/tasks/{taskId}/status")
    public String updateTaskStatus(@PathVariable("taskId") int taskId,
                                   @RequestParam("newStatus") String statusString) {
        Status newStatus = Status.valueOf(statusString);
        projectService.changeTaskStatus(taskId, newStatus);
        return "redirect:/task/" + taskId;
    }

    @GetMapping("/edit/{entity}/{id}")
    public String editForm(@PathVariable String entity, @PathVariable int id, Model model) {
        switch (entity) {
            case "project":
                Project project = projectService.getProject(id);
                model.addAttribute("project", project);
                return "edit-project";
            case "subproject":
                Subproject subproject = projectService.getSubprojectById(id);
                model.addAttribute("subproject", subproject);
                return "edit-subproject";
            case "task":
                Task task = projectService.getTaskById(id);
                model.addAttribute("task", task);
                return "edit-task";
            case "subtask":
                Subtask subtask = projectService.getSubtaskById(id);
                model.addAttribute("subtask", subtask);
                return "edit-subtask";
            default:
                return null;
        }
    }

    @PostMapping("/edit/{entity}/{id}")
    public String editEntity(@PathVariable String entity, @PathVariable int id, @ModelAttribute Object updatedEntity){
        switch (entity) {
            case "project":
                projectService.editProject(id, (Project) updatedEntity);
                break;
            case "subproject":
                projectService.editSubproject(id, (Subproject) updatedEntity);
                break;
            case "task":
                projectService.editTask(id, (Task) updatedEntity);
                break;
            case "subtask":
                projectService.editSubtask(id, (Subtask) updatedEntity);
                break;
        }
        return "redirect:/" + entity + "/" + id;
    }

    @GetMapping("/subproject/{id}")
    public String showSubprojectDetails(@PathVariable int id, Model model) {
        Subproject subproject = projectService.getSubprojectById(id);
        model.addAttribute("subproject", subproject);
        return "subproject-detail";
    }





}
