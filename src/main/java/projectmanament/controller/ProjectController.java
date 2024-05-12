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
                        @RequestParam("password") String password,
                        @RequestParam("username") String name,
                        HttpSession session, Model model){
        int userId = projectService.getIdFromUser(name, password); //todo hvis det kan laves bedre skal det være sådan
        if (projectService.login(userId,password)){
            session.setAttribute("user", new User(userId, name, true, password )); //TODO DEN SKAL IKKE VÆRE TRUE MEN DET FORDI JEG IKKE ANER HVORDAN VI SKAL SE OM DET ER EN ADMIN
            session.setMaxInactiveInterval(30); //30 sekunder
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
    @GetMapping("/teamprojects")
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

    @PostMapping("/teamprojects/{projectId}/status")
    public String updateProjectStatus(@PathVariable("projectId") int projectId, @RequestParam("newStatus") String statusString) {
        Status newStatus = Status.valueOf(statusString);
        projectService.changeProjectStatus(projectId, newStatus);
        return "redirect:/teamprojects";
    }

    /**EDIT**/
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
