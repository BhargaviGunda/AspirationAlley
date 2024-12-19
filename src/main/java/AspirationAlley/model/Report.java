package AspirationAlley.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content; // Reason for the report
    @ManyToOne(cascade = CascadeType.ALL) // Cascade all operations including delete
    @JoinColumn(name = "post_id") // Correctly maps to postId in the database
    private Post post;

	@Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status; // Pending, Approved, Deleted

    public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	// Constructors, Getters, Setters
    public Report() {
        this.createdAt = LocalDateTime.now();
        this.status = ReportStatus.PENDING;
    }

    public Report(User user, String content) {
        this.user = user;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.status = ReportStatus.PENDING;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
    

    // Enum for report status
    public enum ReportStatus {
        PENDING,
        APPROVED,
        DELETED
    }
}
