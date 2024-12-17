package AspirationAlley.controller;

import AspirationAlley.model.User;
import AspirationAlley.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showLoginPage(@RequestParam(value = "customError", required = false) String customError, Model model) {
        if (customError != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        return "login";  // This should match the name of your login template (login.html)
    }

    @PostMapping
    public String loginUser(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        HttpSession session,  // Inject Authentication object to get user info
        Model model
    ) {
        // This part remains, so you can validate manually if needed
        User user = userService.findByUsername(username)
            .orElse(null);

        if (user == null) {
            // Username does not exist
            model.addAttribute("errorUsername", "Invalid username.");
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            // Password is incorrect
            model.addAttribute("errorPassword", "Incorrect password.");
        } else {
            // User authenticated successfully, redirect to the index page
            model.addAttribute("user", user); // Add user to the model
            session.setAttribute("userId",user.getId());
            System.out.println(user);
            return "redirect:/";  // Redirect to index page
        }

        return "login";  // Return to login page with error message
    }
}
