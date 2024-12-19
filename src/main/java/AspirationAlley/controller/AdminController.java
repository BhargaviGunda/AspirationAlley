package AspirationAlley.controller;

import AspirationAlley.model.Post;
import AspirationAlley.model.Report;
import AspirationAlley.model.User;
import AspirationAlley.repository.PostRepository;
import AspirationAlley.service.PostService;
import AspirationAlley.service.ReportService;
import AspirationAlley.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;


    // Admin Dashboard: Display posts, reports, and users
    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        List<Post> posts = postService.getAllPosts();
        List<Report> reports = reportService.getAllReports();
        List<User> users = userService.getAllUsers();

        long userCount = userService.getUserCount();  // Get the number of users
        long postCount = postService.getPostCount();  // Get the number of posts
        long reportCount = reportService.getReportCount();  // Get the number of reports

        // Add the counts to the model
        model.addAttribute("userCount", userCount);
        model.addAttribute("postCount", postCount);
        model.addAttribute("reportCount", reportCount); // Adding report count
        model.addAttribute("posts", posts);
        model.addAttribute("reports", reports);
        model.addAttribute("users", users);

        return "admin-dashboard";
    }

    @GetMapping("/view-post/{id}")
    public String viewPost(@PathVariable("id") Long postId, Model model) {
        Optional<Post> post = postService.getPostById(postId);
        if (post.isEmpty()) {
            model.addAttribute("error", "Post not found");
            return "error";  // Ensure you have an error.html page for this
        }
        model.addAttribute("post", post.get());
        return "view-post";  // Ensure you have a view-post.html page
    }

 // Submit a report (for example, after approving or deleting)
    @PostMapping("/submit-report")
    public String submitReport(@ModelAttribute Report report, BindingResult result) {
        if (result.hasErrors()) {
            return "error";  // Return to an error page or handle as needed
        }

        // Submit the report to the service
        reportService.saveReport(report);

        // Redirect to the view reports page
        return "redirect:/admin/view-reports";  // Redirect to the same page where reports are listed
    }

    // View all reports
    @GetMapping("/view-reports")
    public String viewReports(Model model) {
        List<Report> reports = reportService.getAllReports();
        model.addAttribute("reports", reports);
        return "view-reports";  // Ensure you have a view-reports.html page to display reports
    }
    // Approve report
    @PostMapping("/approve-report/{id}")
    public String approveReport(@PathVariable Long id) {
        reportService.approveReport(id);
        return "redirect:/admin/view-reports";
    }
    // Delete report
    @PostMapping("/delete-report/{id}")
    public String deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return "redirect:/admin/view-reports";
    }

    // View a specific user by their ID
    @GetMapping("/view-user/{id}")
    public String viewUser(@PathVariable("id") Long userId, Model model) {
        User user = userService.getUserById(userId);
        if (user == null) {
            // Add error handling if user not found
            model.addAttribute("error", "User not found");
            return "error"; // Ensure you have an error.html page for this
        }
        model.addAttribute("user", user);
        return "view-user";  // Ensure you have a view-user.html page
    }

    // Delete a user by their ID
    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable("id") Long userId) {
        System.out.println("Deleting post with ID: " + userId);

        userService.deleteUser(userId);
        System.out.println("Deleting post with ID: " + userId);

        return "redirect:/admin/user-management";
    }
    // View all users for management
    @GetMapping("/user-management")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user-management";  // Ensure you have a user-management.html page
    }
    @PostMapping("/approve-post/{id}")
    public String approvePost(@PathVariable Long id) {
        // Logic to approve the post with the provided ID
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/posts")
    public String viewAllPosts(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10); // Create Pageable with 10 posts per page
        Page<Post> postsPage = postRepository.findAll(pageable); // Fetch paginated posts from the repository

        model.addAttribute("posts", postsPage.getContent()); // Add the list of posts to the model
        model.addAttribute("totalPages", postsPage.getTotalPages()); // Add total pages for pagination
        model.addAttribute("currentPage", page); // Add current page number to the model

        return "posts"; // Render the posts.html template
    }

//    @GetMapping("/edit-user/{id}")
//    public String editUser(@PathVariable("id") Long id, Model model) {
//        // Fetch the user by ID
//        User user = userService.findById(id);
//        
//        if (user == null) {
//            // Handle the case when the user is not found, e.g., redirect to an error page
//            return "redirect:/admin/user-list"; // Or any other appropriate page
//        }
//
//        // Add the user to the model so it can be accessed in the edit form
//        model.addAttribute("user", user);
//        
//        // Return the view for editing the user
//        return "edit-user"; // Thymeleaf template for the edit user form
//    }
//    @PostMapping("/save-user")
//    public String saveUser(@ModelAttribute("user") User user) {
//        // Check if password is null or empty
//        if (user.getPassword() == null || user.getPassword().isEmpty()) {
//            // Handle the case where the password is missing, either by showing an error
//            // or setting a default password
//            // For example:
//            user.setPassword("defaultPassword");  // Set a default password (avoid this in production)
//        }
//
//        // Save the user
//        userService.save(user);
//
//        // Redirect to the user list or another page
//        return "user-list";
//    }
    //new new ////
//    @GetMapping("/edit-user/{id}")
//    public String editUser(@PathVariable Long id, Model model) {
//        // Fetch the user by ID and add it to the model
//        User user = userService.findById(id);
//        model.addAttribute("user", user);
//        return "edit-user"; // The name of your Thymeleaf template
//    }
//    @PostMapping("/edit-user/{id}")
//    public String editUserPost(@PathVariable Long id, Model model) {
//        // Fetch the user by ID
//        User user = userService.findById(id);
//
//        if (user == null) {
//            throw new IllegalArgumentException("User with ID " + id + " not found.");
//        }
//
//        // Add the user to the model to populate the form
//        model.addAttribute("user", user);
//
//        // Redirect to the "edit-user" template
//        return "edit-user"; // Name of the Thymeleaf template
//    }
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @PostMapping("/save-user")
//    public String saveUser(@ModelAttribute User user, BindingResult result) {
//        if (result.hasErrors()) {
//            // Handle form errors
//            return "form"; // Return to the form page with error messages
//        }
//
//        // Check if the user already exists by ID
//        if (user.getId() != null) {
//            User existingUser = userService.findById(user.getId());
//            if (existingUser != null) {
//                // Check if the new username is already taken by another user
//                if (!existingUser.getUsername().equals(user.getUsername()) && userService.usernameExists(user.getUsername())) {
//                    result.rejectValue("username", "error.user", "Username already exists. Please choose a different one.");
//                    return "error"; // Return to the form with the error message
//                }
//
//                // Update the existing user details
//                existingUser.setUsername(user.getUsername());
//                existingUser.setEmail(user.getEmail());
//                existingUser.setPhoneNumber(user.getPhoneNumber());
//
//                // Only encode the password if it has changed
//                if (user.getPassword() != null && !user.getPassword().isEmpty() && !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
//                    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
//                }
//
//                // Save the updated user
//                userService.save1(existingUser);
//            } else {
//                throw new IllegalArgumentException("User with ID " + user.getId() + " not found.");
//            }
//        } else {
//            // For new user, check for duplicate username
//            if (userService.usernameExists(user.getUsername())) {
//                result.rejectValue("username", "error.user", "Username already exists. Please choose a different one.");
//                return "form"; // Return to the form with the error message
//            }
//
//            // Encode the password before saving for a new user
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//            
//            // Save the new user
//            userService.save1(user);
//        }
//
//        return "redirect:/admin/dashboard";
//    }
//



//    @GetMapping("/user-list")
//    public String userList(Model model) {
//        List<User> users = userService.findAllUsers(); // Assuming findAllUsers is implemented
//        model.addAttribute("users", users);
//        return "user-list"; // Thymeleaf template to show user list
//    }
//    @PostMapping("/delete-post/{id}")
//    public String deletePost(@PathVariable("id") Long postId) {
//        System.out.println("Deleting post with ID: " + postId);
//
//        // Assuming you have a postService for handling post deletion
//        postService.deletePost(postId);
//
//        return "posts"; // Redirect to the page where posts are listed
//    }
    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            postRepository.delete(post.get());
            return "redirect:/admin/posts"; // or wherever you want to redirect after deletion
        } else {
            // Handle post not found scenario
            return "error"; // or some error handling page
        }
    }



}

