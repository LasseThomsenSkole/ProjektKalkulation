package projectmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import projectmanagement.model.*;
import org.springframework.stereotype.Controller;
import projectmanagement.service.ProjectService;
import org.springframework.ui.Model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("")
public class ProjectController {
    private ProjectService projectService;
    @Autowired

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    private boolean isLoggedIn(HttpSession session){
        return session.getAttribute("user") != null;
    }

    @GetMapping("")
    public String index(HttpSession session, Model model){ //todo kan laves om til at ik at bruge de to nederste metoder
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            /*model.addAttribute("projects", assignmentService.getProjectsForUser(user.getId()));
            model.addAttribute("subprojects", assignmentService.getSubprojectsForUser(user.getId()));
            model.addAttribute("tasks", assignmentService.getTasksForUser(user.getId()));*/
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

   /* @PostMapping("/project/{projectId}")
    public String assignUserToProject(@PathVariable int projectId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        assignmentService.assignUserToProject(user.getId(), projectId);
        return "redirect:/mypage";
    }*/

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
            projectService.createProject(name, description, Date.valueOf(startDate), Date.valueOf(deadline));
        }
        return "redirect:/projects";
    }

    @GetMapping("/subprojects/create")
    public String createSubprojectForm(@RequestParam int parentProjectId, HttpSession session, Model model) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            model.addAttribute("parentProjectId", parentProjectId);
            return "create-subproject";
        }
        return "login";
    }

    @PostMapping("/subprojects/create")
    public String createSubproject(@RequestParam String name, @RequestParam String description, @RequestParam double hours,
                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                                   @RequestParam int parentProjectId, HttpSession session) {
        if (isLoggedIn(session)) {
            projectService.createSubproject(name, description, hours, Date.valueOf(startDate), Date.valueOf(deadline), parentProjectId);
        }
        return "redirect:/project/" + parentProjectId;
    }

    @GetMapping("/tasks/create")
    public String createTaskForm(@RequestParam int subprojectId, HttpSession session, Model model) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            model.addAttribute("subprojectId", subprojectId);
            return "create-task";
        }
        return "login";
    }

    @PostMapping("/tasks/create")
    public String createTask(@RequestParam String name, @RequestParam String description, @RequestParam double hours,
                             @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                             @RequestParam int subprojectId, HttpSession session) {
        if (isLoggedIn(session)) {
            projectService.createTask(name, description, hours, Date.valueOf(startDate), Date.valueOf(deadline), subprojectId);
        }
        return "redirect:/subproject/" + subprojectId;
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

    @GetMapping("/edit/project/{id}")
    public String editProjectForm(@PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Project project = projectService.getProject(id);
            model.addAttribute("project", project);
            return "edit-project";
        }
        return "login";
    }

    @PostMapping("/edit/project/{id}")
    public String editProject(@PathVariable int id, @ModelAttribute Project updatedProject, HttpSession session) {
        if (isLoggedIn(session)) {
            projectService.editProject(id, updatedProject);
            return "redirect:/project/" + id;
        }
        return "login";
    }

    @GetMapping("/edit/subproject/{id}")
    public String editSubprojectForm(@PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Subproject subproject = projectService.getSubprojectById(id);
            model.addAttribute("subproject", subproject);
            return "edit-subproject";
        }
        return "login";
    }

    @PostMapping("/edit/subproject/{id}")
    public String editSubproject(@PathVariable int id, @ModelAttribute Subproject updatedSubproject, HttpSession session) {
        if (isLoggedIn(session)) {
            projectService.editSubproject(id, updatedSubproject);
            return "redirect:/subproject/" + id;
        }
        return "login";
    }

    @GetMapping("/edit/task/{id}")
    public String editTaskForm(@PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Task task = projectService.getTaskById(id);
            model.addAttribute("task", task);
            return "edit-task";
        }
        return "login";
    }

    @PostMapping("/edit/task/{id}")
    public String editTask(@PathVariable int id, @ModelAttribute Task updatedTask, HttpSession session) {
        if (isLoggedIn(session)) {
            projectService.editTask(id, updatedTask);
            return "redirect:/task/" + id;
        }
        return "login";
    }

    @GetMapping("/edit/subtask/{id}")
    public String editSubtaskForm(@PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            Subtask subtask = projectService.getSubtaskById(id);
            model.addAttribute("subtask", subtask);
            return "edit-subtask";
        }
        return "login";
    }

    @PostMapping("/edit/subtask/{id}")
    public String editSubtask(@PathVariable int id, @ModelAttribute Subtask updatedSubtask, HttpSession session) {
        if (isLoggedIn(session)) {
            projectService.editSubtask(id, updatedSubtask);
            return "redirect:/subtask/" + id;
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