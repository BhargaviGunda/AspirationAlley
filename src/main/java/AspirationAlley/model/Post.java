package AspirationAlley.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Base64;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private User author;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "media_data", columnDefinition = "MEDIUMBLOB")
    private byte[] mediaData;
    
    public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	@Column(nullable = false)
    private int likes = 0;

    
    @Transient
    private String profileBase64;

    // Default constructor (required by JPA)
    public Post() {
    }

    // Parameterized constructor
    public Post(User author, String title, String content, LocalDateTime timestamp, byte[] mediaData) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.mediaData = mediaData;
    }

    // Getters and Setters
    public String getBase64MediaData() {
        return mediaData != null ? Base64.getEncoder().encodeToString(mediaData) : null;
    }
    public String getFormattedTimestamp() {
        if (timestamp == null) {
            return "Unknown";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp, now);
        
        long seconds = duration.getSeconds();
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds);
        long days = TimeUnit.SECONDS.toDays(seconds);
        long months = ChronoUnit.MONTHS.between(timestamp, now);
        long years = ChronoUnit.YEARS.between(timestamp, now);

        if (years > 0) {
            return years + " year" + (years > 1 ? "s" : "") + " ago";
        } else if (months > 0) {
            return months + " month" + (months > 1 ? "s" : "") + " ago";
        } else if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else {
            return seconds + " second" + (seconds > 1 ? "s" : "") + " ago";
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getAuthorName() {
        return author != null ? author.getUsername() : "Unknown";
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getMediaData() {
        return mediaData;
    }

    public void setMediaData(byte[] mediaData) {
        this.mediaData = mediaData;
    }
    public String getProfileBase64() {
        return profileBase64;
    }

    public void setProfileBase64(String profileBase64) {
        this.profileBase64 = profileBase64;
    }
    

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author=" + getAuthorName() +
                ", timestamp=" + timestamp +
                ", mediaData=" + (mediaData != null ? "[image data]" : "null") +
                '}';
    }
}
