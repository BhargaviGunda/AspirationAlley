package AspirationAlley.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import AspirationAlley.model.Comment;
import AspirationAlley.model.Post;
import AspirationAlley.repository.CommentRepository;
import AspirationAlley.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    // Fetch comments for a given post ID
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    // Save a new comment and include timestamp
    public Boolean addComment(Long postId, String text, String username, LocalDateTime time) {
    	if (postRepository.existsById(postId)) {
    		Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
    		Comment comment = new Comment();
            comment.setPost(post);
            comment.setUsername(username);
            comment.setText(text);
            comment.setCreatedAt(time); 
            commentRepository.save(comment);
            System.out.println("reported posted");
            return true;
            
        }
        return false;

    }
}