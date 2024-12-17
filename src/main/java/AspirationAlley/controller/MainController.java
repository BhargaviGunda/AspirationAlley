package AspirationAlley.controller;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import AspirationAlley.model.Comment;
import AspirationAlley.model.Image;
import AspirationAlley.model.Post;
import AspirationAlley.model.User;
import AspirationAlley.repository.UserRepository;
import AspirationAlley.service.CommentService;
import AspirationAlley.service.EmailService;
import AspirationAlley.service.ImageService;
import AspirationAlley.service.PostService;
import AspirationAlley.service.UserService;
import jakarta.servlet.http.HttpSession;


@Controller
public class MainController {
	@Autowired
	private PostService postService;
	@Autowired
	private ImageService imageService;
	@Autowired
    private UserService userService;
	@Autowired
    private EmailService emailService;
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/report-user")
	public String showReportPage(Model model, HttpSession session) {
		Long userId=(Long)session.getAttribute("userId");
		User user = userRepository.findById(userId).orElse(null);
		if (user!=null)
			model.addAttribute("isLoggedIn", true);
	    return "report-user";
	}
	@GetMapping("/about")
	public String showAboutPage(Model model, HttpSession session) {
		Image logoImage = imageService.getImageById(7);
        String base64Logo = Base64.getEncoder().encodeToString(logoImage.getImage());
    	model.addAttribute("logoImageBase64", base64Logo);
    	Long userId=(Long)session.getAttribute("userId");
    	if(userId!=null)
    		model.addAttribute("isLoggedIn", true);
    	else {
    		model.addAttribute("isLoggedIn", false);
    	}
	    return "about";
	}
	@GetMapping("/contact")
	public String showContactPage(Model model, HttpSession session) {
		Image logoImage = imageService.getImageById(7);
        String base64Logo = Base64.getEncoder().encodeToString(logoImage.getImage());
    	model.addAttribute("logoImageBase64", base64Logo);
    	Long userId=(Long)session.getAttribute("userId");
    	if(userId!=null)
    		model.addAttribute("isLoggedIn", true);
    	else {
    		model.addAttribute("isLoggedIn", false);
    	}
	    return "contact";
	}
 
    @GetMapping("/")
    public String showIndexPage(Model model, HttpSession session) {
    	Long userId=(Long)session.getAttribute("userId");
    	
        Image logoImage = imageService.getImageById(7);
        String base64Logo = Base64.getEncoder().encodeToString(logoImage.getImage());
    	model.addAttribute("logoImageBase64", base64Logo);
    	
    	Image profileImage = imageService.getImageById(6);
        String base64Profile = Base64.getEncoder().encodeToString(profileImage.getImage());
        model.addAttribute("profileImageBase64", base64Profile); 
        
        
        List<String> streakDates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(DayOfWeek.SUNDAY); 
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            streakDates.add(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 1));
        }

        model.addAttribute("streakDates", streakDates);

        
    	//unregistered user 
    	if (userId == null) {
    		List<Post> posts = postService.getAllPosts();
    		Collections.reverse(posts);
            for (Post post : posts) {
                User author = post.getAuthor();
                if (author != null) {
                    String profileBase64 = author.getProfilePicture() != null? Base64.getEncoder().encodeToString(author.getProfilePicture()): base64Profile;
                    post.setProfileBase64(profileBase64); // Set profile picture for this post
                }
            }
            model.addAttribute("posts", posts);
            
            List<Boolean> activeStreak = List.of(false, false, false, false, false, false, false);
            model.addAttribute("activeStreak", activeStreak);
            
            model.addAttribute("isLoggedIn", false);
                
            return "unreg-page";

        }
    	
    	
    	// registered user
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
        	model.addAttribute("isLoggedIn", true);
        	List<Post> posts = postService.getAllPosts();
        	Collections.reverse(posts); // Reverse the list
        	model.addAttribute("posts", posts);
            model.addAttribute("user", user); 
            model.addAttribute("streakCount", user.getStreakCount());
            for (Post post : posts) {
                User author = post.getAuthor();
                if (author != null) {
                    String profileBase64 = author.getProfilePicture() != null? Base64.getEncoder().encodeToString(author.getProfilePicture()): base64Profile;
                    post.setProfileBase64(profileBase64); // Set profile picture for this post
                }
            }
            List<Boolean> activeStreak = userService.calculateActiveStreak(user);
            //System.out.println(activeStreak);
            List<String> streakDates1 = userService.getStreakDates();
            model.addAttribute("activeStreak", activeStreak);
            model.addAttribute("streakDates", streakDates1);
            return "index"; 
        
        } else {
            model.addAttribute("error", "User not found.");
        }
        return "login";
        
            
    }
    
    
    @PostMapping("/report-user")
    public String submitReport(
            @RequestParam String username,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {

        // Validate the username
        boolean userExists = userService.doesUserExist(username);
        if (!userExists) {
            redirectAttributes.addFlashAttribute("error", "user does not exist.");
            return "redirect:/report-user"; // Redirect back to the report form
        }
        else {
        	redirectAttributes.addFlashAttribute("reportMessage", "Report submitted successfully!");
        	try {
                // Send email to the admin
                String adminEmail = "22b01a1228@svecw.edu.in";  // Replace with admin email
                String subject = "New Report to " + username;
                String body = "<p>You have a new report to: " + username +"</p>"
                            + "<p><strong>reprt reason:</strong></p>"
                            + "<p>" + content + "</p>";
                
                // Call the email service to send the email
                emailService.sendEmail(adminEmail, subject, body);
            } catch (Exception e) {
                e.printStackTrace();  // Handle the exception (you could log it, etc.)
                redirectAttributes.addFlashAttribute("message", "There was an error sending the email.");
                return "redirect:/report-user";
            }

            // Return the redirect after the email has been sent successfully
            return "redirect:/";
        }
        
    }
    @PostMapping("/contact")
    public String ContactUs(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String content,
            RedirectAttributes redirectAttributes,
            Model model, HttpSession session) {

        // Validate the username
        boolean userExists = userService.doesUserExist(username);
        boolean emailExists = userService.doesEmailExist(email);
        
        // Check if the user and email exist
        if (!userExists || !emailExists) {
            redirectAttributes.addFlashAttribute("message", "User or Email does not exist.");
            return "redirect:/contact"; // Redirect back to the contact page
        } else {
            // Success message
            redirectAttributes.addFlashAttribute("message", "Message submitted successfully!");

            try {
                // Send email to the admin
                String adminEmail = "22b01a1228@svecw.edu.in";  // Replace with admin email
                String subject = "New Contact Us Message from " + username;
                String body = "<p>You have a new message from: " + username + " (" + email + ")</p>"
                            + "<p><strong>Message:</strong></p>"
                            + "<p>" + content + "</p>";
                
                // Call the email service to send the email
                emailService.sendEmail(adminEmail, subject, body);
            } catch (Exception e) {
                e.printStackTrace();  // Handle the exception (you could log it, etc.)
                redirectAttributes.addFlashAttribute("message", "There was an error sending the email.");
                return "redirect:/contact";
            }

            // Return the redirect after the email has been sent successfully
            return "redirect:/contact";
        }
    }
    @PostMapping("/posts/{postId}/like")
    @ResponseBody
    public int likePost(@PathVariable Long postId,@RequestParam Boolean isLiked) {
    	Post updatedPost;
    	if(isLiked) {
    		updatedPost = postService.incrementLikes(postId);
    	}
    	else
    	{
    		updatedPost = postService.decrementLikes(postId);
    	}
        return updatedPost.getLikes(); // Return updated like count
    }
    @Autowired
    private CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public String getComments(@PathVariable Long postId) {
        return "Hii";
    }

   
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<String> addComment(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        String text = request.get("text");
        Long userId = Long.parseLong(request.get("userid"));
        String username=postService.getUserName(userId);
        LocalDateTime time = LocalDateTime.now();
        boolean commentPosted = commentService.addComment(postId, text, username, time);
        if (commentPosted) {
            return ResponseEntity.ok("Commented successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Failed to comment.");
        }
    }
    
}