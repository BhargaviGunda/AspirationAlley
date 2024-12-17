package AspirationAlley.controller;

import AspirationAlley.model.User;
import AspirationAlley.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    // Display the registration page
    @GetMapping
    public String showRegistrationPage() {
        return "register";  // This should match the name of your registration template (register.html)
    }

    // Handle registration form submission
    @PostMapping
    public String registerUser(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        @RequestParam("phoneNumber") String phoneNumber,
        @RequestParam("email") String email,
        Model model
    ) {
        boolean hasErrors = false;

        // Validation checks
        if (!username.matches("^[a-z0-9._]+$")) {
            model.addAttribute("errorUsername", "Username can only contain lowercase letters, numbers, underscores, or dots.");
            hasErrors = true;
        }
        if (userService.usernameExists(username)) {
            model.addAttribute("errorUsername", "Username already exists.");
            hasErrors = true;
        }

        if (userService.emailExists(email)) {
            model.addAttribute("errorEmail", "Email already exists.");
            hasErrors = true;
        }

        if (password.isEmpty()) {
            model.addAttribute("errorPassword", "Password is required.");
            hasErrors = true;
        }

        if (!phoneNumber.matches("\\d{10}")) {
            model.addAttribute("errorPhoneNumber", "Phone number must be 10 digits.");
            hasErrors = true;
        }

        if (!hasErrors) {
            try {
                userService.registerUser(username, password, phoneNumber, email);
                model.addAttribute("success", "Registration successful. Please log in.");
                return "redirect:/login";
            } catch (RuntimeException e) {
                model.addAttribute("error", "Failed to register");
                return "register";
            }
        }
        model.addAttribute("error", "Failed to register.Check credentials!!");
        return "register";
    }
}
