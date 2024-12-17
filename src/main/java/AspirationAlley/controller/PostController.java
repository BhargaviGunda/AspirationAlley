package AspirationAlley.controller;

import AspirationAlley.model.Post;
import AspirationAlley.service.PostService;
import AspirationAlley.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @GetMapping("/create-post")
    public String showCreatePostForm() {
        return "create-post";
    }

    @PostMapping("/create-post")
    public String createPost(
    		@RequestParam String author,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam("media") MultipartFile media,
            RedirectAttributes redirectAttributes) throws Exception {
    	if (author == null || author.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Username name cannot be empty.");
            return "redirect:/create-post"; // Redirect back to the post creation page
        }
    	boolean authorExists = userService.doesUserExist(author);
        if (!authorExists) {
            redirectAttributes.addFlashAttribute("message", "The author does not exist. Please use a valid author.");
            return "redirect:/create-post"; // Redirect back to the form
        }

        byte[] mediaData = media.getBytes();  // Get image data as a byte array
        LocalDateTime now = LocalDateTime.now();  // Get the current time

        postService.savePost( author,title, content, now, mediaData);  // Save post to the database

        //RedirectAttributes redirectAttributes;
		redirectAttributes.addFlashAttribute("message", "Post created successfully!");

        return "redirect:/";
    }
   
}

