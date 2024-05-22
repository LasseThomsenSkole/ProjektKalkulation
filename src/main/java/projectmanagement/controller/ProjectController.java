package projectmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import projectmanagement.model.*;
import org.springframework.stereotype.Controller;
import projectmanagement.service.ProjectService;
import org.springframework.ui.Model;
import projectmanagement.service.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

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
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            List<Project> userProjects = projectService.getProjectsForUser(user.getId());
            model.addAttribute("user", user);
            model.addAttribute("projects", userProjects);
            return "index";
        }
        return "login";
    }


   @PostMapping("/project/{projectId}")
    public String assignUserToProject(@PathVariable int projectId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        projectService.assignUserToProject(user.getId(), projectId);
        return "redirect:/mypage";
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

    /**CREATE**/
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
            int projectID = projectService.createProject(name, description, Date.valueOf(startDate), Date.valueOf(deadline));
            projectService.assignUserToProject(((User) session.getAttribute("user")).getId(), projectID);
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
    public String createSubproject(@RequestParam String name, @RequestParam String description,
                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                                   @RequestParam int parentProjectId, HttpSession session) {
        if (isLoggedIn(session)) {
            projectService.createSubproject(name, description, Date.valueOf(startDate), Date.valueOf(deadline), parentProjectId);
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
    public String createTask(@RequestParam String name, @RequestParam String description,
                             @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                             @RequestParam int subprojectId, HttpSession session) {
        if (isLoggedIn(session)) {
            projectService.createTask(name, description, Date.valueOf(startDate), Date.valueOf(deadline), subprojectId);
        }
        return "redirect:/subproject/" + subprojectId;
    }

    @GetMapping("/subtask/create")
    public String createSubtaskForm(@RequestParam int parentTaskId, HttpSession session, Model model) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            model.addAttribute("parentTaskId", parentTaskId);
            return "create-subtask";
        }
        return "login";
    }

    @PostMapping("/subtask/create")
    public String createSubtask(@RequestParam String name, @RequestParam String description,@RequestParam Double hours,
                                @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                                @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam int parentTaskId, HttpSession session) {
        if (isLoggedIn(session)) {
            projectService.createSubtask(name, description, hours,Date.valueOf(startDate), Date.valueOf(deadline), parentTaskId);
        }
        return "redirect:/projects";
    }

    /**DETAIL**/
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

//    @GetMapping("/task/{id}")
//    public String showTaskDetails(@PathVariable int id, Model model, HttpSession session) {
//        if (isLoggedIn(session)) {
//            User user = (User) session.getAttribute("user");
//            model.addAttribute("user", user);
//            Task task = projectService.getTaskById(id);
//            model.addAttribute("task", task);
//            return "task-detail";
//        }
//        return "login";
//    }


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

    /**ARCHIVED**/
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

    /**STATUS**/
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
            int subprojectId = projectService.findSubprojectIdByTaskId(taskId);

            return "redirect:/subproject/" + subprojectId;
        }
        return "login";
    }

    /**EDIT**/
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
            int subprojectId = projectService.findSubprojectIdByTaskId(id);
            return "redirect:/subproject/" + subprojectId;
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

    //vi skal lave en overordnet dto som skal extende dto for hvert objekt hvis det her skal virke
    /*@GetMapping("/edit/{entity}/{id}")
    public String editForm(@PathVariable String entity, @PathVariable int id, Model model, HttpSession session) {
        if (isLoggedIn(session)) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            model.addAttribute("entity", entity);
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
                    if (updatedEntity instanceof Project) {
                        projectService.editProject(id, (Project) updatedEntity);
                    }
                    break;
                case "subproject":
                    if (updatedEntity instanceof Subproject) {
                        projectService.editSubproject(id, (Subproject) updatedEntity);
                    }
                    break;
                case "task":
                    if (updatedEntity instanceof Task) {
                        projectService.editTask(id, (Task) updatedEntity);
                    }
                    break;
                case "subtask":
                    if (updatedEntity instanceof Subtask) {
                        projectService.editSubtask(id, (Subtask) updatedEntity);
                    }
                    break;
            }
            return "redirect:/" + entity + "/" + id;
        }
        return "login";
    }*/


    /**DELETE**/
    @PostMapping("/projects/{projectId}/delete")
    public String deleteProject(@PathVariable int projectId, HttpSession session) {
        if (isLoggedIn(session) && isAdmin(session)) {
            projectService.deleteProject(projectId);
            return "redirect:/projects";
        }
        return "login";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.isAdmin();
    }
}
