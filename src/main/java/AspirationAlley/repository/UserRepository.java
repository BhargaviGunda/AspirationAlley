
package AspirationAlley.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import AspirationAlley.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email); // Check for duplicate emails
    
    Optional<User> findByUsername(String username); // Return Optional<User> for better null handling

    Optional<User> findByEmail(String email); // Return an Optional<User> to handle absence of the user

}
