package AspirationAlley.service;

import AspirationAlley.model.User;
import AspirationAlley.repository.UserRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String username, String password, String phoneNumber, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    public List<Boolean> calculateActiveStreak(User user) {
        // Get today's date and the start of the current week (most recent Sunday)
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        // Get the user's last post date and streak count
        LocalDate lastPostDate = user.getLastPostDate();
        int streakCount = user.getStreakCount();

        // Initialize active streak days
        List<Boolean> activeStreak = new ArrayList<>(List.of(false, false, false, false, false, false, false));

        // Validate inputs
        if (lastPostDate == null || streakCount <= 0) {
            System.out.println("No valid streak data available.");
            return activeStreak; // All false if no streak data
        }

        // Iterate through the streak count and mark active days
        LocalDate currentStreakDate = lastPostDate;
        for (int i = 0; i < streakCount; i++) {
            if (currentStreakDate.isBefore(startOfWeek) || currentStreakDate.isAfter(startOfWeek.plusDays(6))) {
                break; // Skip dates outside the current week
            }
            int dayIndex = (currentStreakDate.getDayOfWeek().getValue() % 7); // Map Sunday = 0
            activeStreak.set(dayIndex, true); // Mark the day as active
            currentStreakDate = currentStreakDate.minusDays(1); // Move to the previous day in the streak
        }

        // Debug logs
//        System.out.println("Start of Week: " + startOfWeek);
//        System.out.println("Last Post Date: " + lastPostDate);
//        System.out.println("Streak Count: " + streakCount);
//        System.out.println("Active Streak: " + activeStreak);

        return activeStreak;
    }



    public List<String> getStreakDates() {
        // Generate days of the week starting from Sunday
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
        List<String> streakDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            streakDates.add(
                startOfWeek.plusDays(i)
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    .substring(0, 1)
            );
        }
        return streakDates;
    }
    public boolean updateUserProfile(Long userId, String email, String phoneNumber) {
        try {
            // Validate email
            if (!isValidEmail(email)) {
                return false;
            }

            if (!isValidPhoneNumber(phoneNumber)) {
            	return false;
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            userRepository.save(user); // Save updated user to the database
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to validate email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    // Helper method to validate phone number
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Assuming a valid phone number is 10 digits long
        String phoneRegex = "^[0-9]{10}$";
        return phoneNumber.matches(phoneRegex);
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
    public boolean doesUserExist(String author) {
        return userRepository.existsByUsername(author);
    }
    public boolean doesEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by their ID
    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);  // return null if user not found
    }
 // Delete a user by their ID
    public void deleteUser(Long userId) {
    			System.out.println(userId);
        userRepository.deleteById(userId);
    }
 // Method to find user by ID
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Method to save or update the user
   
 // Method to get all users
    public List<User> findAllUsers() {
        return userRepository.findAll(); // This will return all users from the database
    }
    public User save(User user) {
        // Ensure the password is not null and is hashed
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
        }
        
        System.out.println("Received user with phone number: " + user.getPhoneNumber());
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        // pr
        // Save the user to the repository
        return userRepository.save(user);
    }
    public void save1(User user) {
        // Check if user exists in the database (use ID to check existence)
        User existingUser = (user.getId() != null) ? userRepository.findById(user.getId()).orElse(null) : null;

        if (existingUser != null) {
            // Check if the new username already exists in the database and belongs to another user
            if (!existingUser.getUsername().equals(user.getUsername()) && userRepository.existsByUsername(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists. Please choose a different one.");
            }

            // Update fields for the existing user
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhoneNumber(user.getPhoneNumber());

            // Update password only if it has been changed (not encoded yet)
            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // Save the updated user
            userRepository.save(existingUser);

        } else {
            // Check if the username already exists in the database
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists. Please choose a different one.");
            }

            // For new user, encode the password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Print phone number for debugging
            System.out.println("Phone Number: " + user.getPhoneNumber());

            // Check for null or empty phone number
            if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
                throw new IllegalArgumentException("Phone number cannot be null or empty.");
            }

            // Save the new user
            userRepository.save(user);
        }
    }
    public long getUserCount() {
        return userRepository.count(); // Return the number of users in the database
    }
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email); // Return Optional<User> from the repository
    }
    public void updatePassword(User user, String newPassword) {
        // Encode the new password and set it
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // Save the user with the updated password
        userRepository.save(user);
    }


    // Add more service methods as needed
}
