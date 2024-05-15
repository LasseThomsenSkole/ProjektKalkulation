package projectmanament.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import projectmanament.model.User;

@Controller
@RequestMapping("/assign")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    @PostMapping("/project/{projectId}")
    public String assignUserToProject(@PathVariable int projectId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        assignmentService.assignUserToProject(user.getId(), projectId);
        return "redirect:/mypage";
    }

    @PostMapping("/subproject/{subprojectId}")
    public String assignUserToSubproject(@PathVariable int subprojectId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        assignmentService.assignUserToSubproject(user.getId(), subprojectId);
        return "redirect:/mypage";
    }

    @PostMapping("/task/{taskId}")
    public String assignUserToTask(@PathVariable int taskId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        assignmentService.assignUserToTask(user.getId(), taskId);
        return "redirect:/mypage";
    }
}
