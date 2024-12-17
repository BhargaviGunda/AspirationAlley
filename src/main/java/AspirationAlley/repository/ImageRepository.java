package AspirationAlley.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import AspirationAlley.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // Query to retrieve the image blob by name
    @Query("SELECT i.image FROM Image i WHERE i.name = :name")
    byte[] findImageByName(@Param("name") String name);

}
