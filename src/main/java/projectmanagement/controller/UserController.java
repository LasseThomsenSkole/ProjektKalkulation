package projectmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projectmanagement.model.Project;
import projectmanagement.model.User;
import projectmanagement.service.ProjectService;
import projectmanagement.service.UserService;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;


    private boolean isLoggedIn(HttpSession session){
        return session.getAttribute("user") != null;
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
        if (userService.login(name,password)){
            session.setAttribute("user", userService.getUserFromName(name));
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
    //TODO TILFØJ INDIKATOR FOR HVIS DER ALLEREDE ER EN USER MED DET NAVN
    @PostMapping("/create-account") //TODO kan ikke indsætte header
    public String createAccountPost(@RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    HttpSession session, Model model){
        if (isLoggedIn(session)){
            User user = (User) session.getAttribute("user");
            if (user.isAdmin()){
                if (userService.userAlreadyExists(username)){
                    model.addAttribute("userAlreadyExists", true);
                    return "create-account";
                }
                userService.insertUser(username, password);
                return "/create-account";
            }
        }
        return "redirect:/login";
    }


    @GetMapping("/logout")
    public String logOut(HttpSession session) {
        session.invalidate();
        return "login";
    }

}
