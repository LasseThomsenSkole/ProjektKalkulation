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
            session.setMaxInactiveInterval(300); // 5 minutter
            return "redirect:/";
        }
        model.addAttribute("wrongCredentials", true);
        return "login";
    }

    @GetMapping("/create-account")
    public String createAccount(HttpSession session){
        if (isLoggedIn(session)){
            User user = (User) session.getAttribute("user");
            return user.isAdmin() ? "create-account" : "login"; //hvis man er admin kan man oprette accounts
        }
        return "login";
    }

    @PostMapping("/create-account")
    public String createAccountPost(@RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    HttpSession session, Model model){
        if (isLoggedIn(session)){ //det her er fuld spaghetti kode men jeg kan ikke finde en bedre måde at gøre det på - Lasse
            User user = (User) session.getAttribute("user");
            if (user.isAdmin()){
                if (projectService.userAlreadyExists(username)){
                    model.addAttribute("userAlreadyExists", true);
                    return "create-account";
                }
                projectService.insertUser(username, password);
                return "redirect:/login"; //todo måske redirect tilbage til createaccount ????? - lasse
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logOut(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @GetMapping("/projects")
    public String showAllProjects(@RequestParam(value = "sort", required = false) String sort, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            List<Project> projects;
            if (sort != null) {
                projects = projectService.findAllProjectsSorted(sort);
            } else {
                projects = projectService.findAllProjects();
            }
            model.addAttribute("projects", projects);
            return "projects";
        }
        return "login";
    }

    @GetMapping("/projects/create")
    public String createProjectForm(HttpSession session, Model model) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            return "create-project";
        }
        return "login";
    }

    @PostMapping("/projects/create")
    public String createProject(@RequestParam String name, @RequestParam String description,
                                @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                                @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            int projectId = projectService.createProject(name, description, Date.valueOf(startDate), Date.valueOf(deadline));
            projectService.createProjectRelation(user.getId(), projectId);
        }
        return "redirect:/projects";
    }

    @GetMapping("/project/{id}")
    public String showProjectDetails(@PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Project project = projectService.getProject(id);
            model.addAttribute("project", project);
            return "project-detail";
        }
        return "login";
    }

    @GetMapping("/archivedprojects")
    public String showArchivedProjects(Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            List<Project> archivedProjects = projectService.findArchivedProjects();
            model.addAttribute("archivedProjects", archivedProjects);
            return "archived-projects";
        }
        return "login";
    }

    @PostMapping("/projects/{projectId}/status")
    public String updateProjectStatus(@PathVariable("projectId") int projectId, @RequestParam("newStatus") String statusString, HttpSession session, Model model) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Status newStatus = Status.valueOf(statusString);
            projectService.changeProjectStatus(projectId, newStatus);
            return "redirect:/projects";
        }
        return "login";
    }

    @PostMapping("/subprojects/{subprojectId}/status")
    public String updateSubprojectStatus(@PathVariable("subprojectId") int subprojectId,
                                         @RequestParam("newStatus") String statusString, HttpSession session, Model model) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Status newStatus = Status.valueOf(statusString);
            projectService.changeSubprojectStatus(subprojectId, newStatus);
            return "redirect:/subproject/" + subprojectId;
        }
        return "login";
    }

    @PostMapping("/tasks/{taskId}/status")
    public String updateTaskStatus(@PathVariable("taskId") int taskId,
                                   @RequestParam("newStatus") String statusString, HttpSession session, Model model) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Status newStatus = Status.valueOf(statusString);
            projectService.changeTaskStatus(taskId, newStatus);
            return "redirect:/task/" + taskId;
        }
        return "login";
    }

    @GetMapping("/edit/{entity}/{id}")
    public String editForm(@PathVariable String entity, @PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
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
                    return "redirect:/projects";
            }
        }
        return "login";
    }

    @PostMapping("/edit/{entity}/{id}")
    public String editEntity(@PathVariable String entity, @PathVariable int id, @ModelAttribute Object updatedEntity, HttpSession session, Model model) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
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
        return "login";
    }

    @GetMapping("/subproject/{id}")
    public String showSubprojectDetails(@PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Subproject subproject = projectService.getSubprojectById(id);
            model.addAttribute("subproject", subproject);
            return "subproject-detail";
        }
        return "login";
    }

    @GetMapping("/task/{id}")
    public String showTaskDetails(@PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Task task = projectService.getTaskById(id);
            model.addAttribute("task", task);
            return "task-detail";
        }
        return "login";
    }

    @GetMapping("/subtask/{id}")
    public String showSubtaskDetails(@PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Subtask subtask = projectService.getSubtaskById(id);
            model.addAttribute("subtask", subtask);
            return "subtask-detail";
        }
        return "login";
    }
}
