package AspirationAlley.controller;

import AspirationAlley.service.UserService;
import AspirationAlley.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Optional;
import java.util.Random;

@Controller
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String generatedOtp;

    @GetMapping
    public String showForgotPasswordPage() {
        return "forgot-password"; // Template for OTP input
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email, Model model) throws MessagingException {
        Optional<User> userOptional = userService.findUserByEmail(email);

        if (userOptional.isEmpty()) {
            model.addAttribute("error", "No user found with that email address.");
            return "forgot-password";
        }

        generatedOtp = String.format("%04d", new Random().nextInt(10000)); // Generate a 4-digit OTP

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Your OTP for Password Reset");
        helper.setText("Your OTP is: " + generatedOtp, true);
        mailSender.send(message);

        model.addAttribute("email", email);
        return "enter-otp"; // Template for verifying OTP
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp, Model model) {
        if (generatedOtp != null && generatedOtp.equals(otp)) {
            model.addAttribute("email", email);
            return "reset-password"; // Template for password reset
        }
        model.addAttribute("error", "Invalid OTP.");
        return "enter-otp";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {
        Optional<User> userOptional = userService.findUserByEmail(email);

        if (userOptional.isEmpty()) {
            model.addAttribute("error", "No user found with that email address.");
            return "reset-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "reset-password";
        }

        userService.updatePassword(userOptional.get(), newPassword);
        model.addAttribute("message", "Your password has been reset successfully.");
        return "login"; // Redirect to login page
    }
}