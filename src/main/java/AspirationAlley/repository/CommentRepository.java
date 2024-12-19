package AspirationAlley.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import AspirationAlley.model.Comment;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    @Transactional
    void deleteByPostId(Long postId);
}