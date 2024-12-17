package AspirationAlley.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import AspirationAlley.model.Post;
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByAuthor_Id(Long userId);
    Page<Post> findAll(Pageable pageable);

}

