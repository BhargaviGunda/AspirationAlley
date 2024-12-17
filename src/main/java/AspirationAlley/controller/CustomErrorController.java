package AspirationAlley.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.NoHandlerFoundException;

@Controller
public class CustomErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    // Handle general errors
    @RequestMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        return "error"; // Return the name of the error page template
    }

    // Handle undefined routes
    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public String handleUndefinedRoutes(Model model) {
        model.addAttribute("errorMessage", "Page not found. The URL you requested does not exist.");
        return "error"; // Return the name of the error page template
    }


    public String getErrorPath() {
        return "/error"; // Path for handling errors
    }

    // Global exception handler
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error"; // Return the name of the error page template
    }
}
