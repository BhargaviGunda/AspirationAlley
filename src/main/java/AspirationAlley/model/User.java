package AspirationAlley.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Collections;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,name="phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String email;


    @Column(name = "profile_picture", columnDefinition = "MEDIUMBLOB")
    private byte[] profilePicture;
    
    @Column(name = "streak_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    
    private int streakCount; // Add this field   
    private String role="user";

    @Column(name = "last_post_date")
    private LocalDate lastPostDate;
    // Default constructor (required by JPA)
    public User() {
    }

    // Parameterized constructor
    public User(String username, String password, String phoneNumber, String email, byte[] profilePicture) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    // Helper method to get the profile picture as a Base64 encoded string
    public String getBase64ProfilePicture() {
        return profilePicture != null ? Base64.getEncoder().encodeToString(profilePicture) : null;
    }
    
    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }
    
    public LocalDate getLastPostDate() {
        return lastPostDate;
    }

    public void setLastPostDate(LocalDate lastPostDate) {
        this.lastPostDate = lastPostDate;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture=" + (profilePicture != null ? "[profile picture data]" : "null") +
                '}';
    }
}
