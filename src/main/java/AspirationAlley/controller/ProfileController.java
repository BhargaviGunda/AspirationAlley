package AspirationAlley.controller;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import AspirationAlley.model.Image;
import AspirationAlley.model.Post;
import AspirationAlley.model.Report;
import AspirationAlley.model.User;
import AspirationAlley.repository.PostRepository;
import AspirationAlley.repository.UserRepository;
import AspirationAlley.service.ImageService;
import AspirationAlley.service.PostService;
import AspirationAlley.service.ReportService;
import AspirationAlley.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ReportService reportService;

    // Helper method to set common profile data
    private void setProfileData(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Image logoImage = imageService.getImageById(7);
        String base64Logo = Base64.getEncoder().encodeToString(logoImage.getImage());
        model.addAttribute("logoImageBase64", base64Logo);

        Image profileImage = imageService.getImageById(6);
        String base64Profile = Base64.getEncoder().encodeToString(profileImage.getImage());
        model.addAttribute("profileImageBase64", base64Profile);

        if (userId == null) {
            model.addAttribute("isLoggedIn", false);
            return;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<Post> posts = postService.getPostsByUserId(userId);
            Collections.reverse(posts);
            for (Post post : posts) {
                User author = post.getAuthor();
                if (author != null) {
                    String profileBase64 = author.getProfilePicture() != null ? Base64.getEncoder().encodeToString(author.getProfilePicture()) : base64Profile;
                    post.setProfileBase64(profileBase64); // Set profile picture for this post
                }
            }

            List<Boolean> activeStreak = userService.calculateActiveStreak(user);
            List<String> streakDates = userService.getStreakDates();

            model.addAttribute("activeStreak", activeStreak);
            model.addAttribute("streakDates", streakDates);
            model.addAttribute("posts", posts);
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("user", user);
        }
    }

    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        // Call the helper method to set profile data
        setProfileData(model, session);
        return "profile"; // Assuming your profile page is named 'profile.html'
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam Long userId,
            @RequestParam String email,
            @RequestParam String phoneNumber,
            Model model,
            HttpSession session) {

        System.out.println("Received data: userId=" + userId + ", email=" + email + ", phoneNumber=" + phoneNumber);

        boolean updateSuccess = userService.updateUserProfile(userId, email, phoneNumber);

        if (updateSuccess) {
            model.addAttribute("message", "Profile updated successfully");
        } else {
            model.addAttribute("message", "Failed to update due to wrong mail or phn number");
        }

        // After update, call the helper method to refresh the profile data
        setProfileData(model, session);

        return "profile"; // Return the same profile page after updating
    }
    @PostMapping("/change-password")
    public String updatePassword(@RequestParam Long userId, 
            @RequestParam String currentPassword, 
            @RequestParam String newPassword, 
            @RequestParam String confirmNewPassword,
    		Model model,
            HttpSession session
            ) {
        
        // Update password logic
        boolean updateSuccess=userService.changePassword(userId, currentPassword, newPassword);
        if (updateSuccess) {
            model.addAttribute("message", "Password updated successfully.");
        } else {
            model.addAttribute("message", "Failed to update the password because the current password is incorrect.");
        }

        // After update, call the helper method to refresh the profile data
        setProfileData(model, session);

        return "profile";
    }
    @PostMapping("/reportPost/{postId}")
    public ResponseEntity<?> reportPost(@PathVariable Long postId, @RequestParam Long userId) {
        try {
            // Retrieve the User and Post objects from the database using their IDs
            User user = userService.findById(userId); // Assuming userService has a method to fetch a user by ID
            Post post = postService.findById(postId); // Assuming postService has a method to fetch a post by ID

            if (user == null || post == null) {
                return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                        .body(Map.of("success", false, "message", "User or Post not found"));
            }

            // Create a new Report object
            Report report = new Report(user, "Reason for the report"); // Pass the User and reason to the constructor
            report.setPost(post); // Set the Post object

            // Save the report in the database
            reportService.saveReport(report);

            // Return a success response
            return ResponseEntity.ok(Map.of("success", true, "message", "Post reported successfully!"));
        } catch (Exception e) {
            // Return a failure response
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to report the post: " + e.getMessage()));
        }
    }

}
