package AspirationAlley.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import AspirationAlley.model.Image;
import AspirationAlley.repository.ImageRepository;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public byte[] getImage(String name) {
        return imageRepository.findImageByName(name);
    }

    public List<Image> getAllImages() {
        return imageRepository.findAll(); // Fetch all images
    }
    public Image getImageById(int i) {
        return imageRepository.findById((long) i)
            .orElseThrow(() -> new RuntimeException("Image with ID " + i + " not found"));
    }

}
