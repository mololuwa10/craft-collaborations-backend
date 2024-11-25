package com.example.dissertation_backend.solution.Customers.Controllers;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.Customers.Service.ArtisanProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/artisan")
@CrossOrigin(
  origins = { "*" },
  methods = {
    RequestMethod.OPTIONS,
    RequestMethod.GET,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.POST,
  }
)
public class ArtisanProfileController {

  @Autowired
  private ArtisanProfileService artisanProfileService;

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/")
  public String helloArtisanController() {
    return "Artisan access level";
  }

  @GetMapping("/{id}")
  public ResponseEntity<ArtisanProfile> getArtisanProfileById(
    @PathVariable Integer id
  ) {
    return artisanProfileService
      .getArtisanProfileById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ArtisanProfile createArtisanProfile(
    @RequestBody ArtisanProfile artisanProfile
  ) {
    return artisanProfileService.saveOrUpdateArtisanProfile(artisanProfile);
  }

  @PutMapping
  public ResponseEntity<ArtisanProfile> updateArtisanProfile(
    @RequestParam(
      value = "profilePicture",
      required = false
    ) MultipartFile profilePicture,
    @RequestParam(
      value = "storeBanner",
      required = false
    ) MultipartFile storeBanner,
    @RequestParam(
      value = "galleryImages",
      required = false
    ) List<MultipartFile> galleryImages,
    @RequestParam("artisanProfile") String artisanProfileStr,
    Principal principal
  ) throws IOException {
    ApplicationUser currentUser = userRepository
      .findByUsername(principal.getName())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (currentUser == null) {
      throw new UsernameNotFoundException("User not found");
    }

    ObjectMapper mapper = new ObjectMapper();
    ArtisanProfile artisanProfile = mapper.readValue(
      artisanProfileStr,
      ArtisanProfile.class
    );

    if (profilePicture != null && !profilePicture.isEmpty()) {
      String profilePictureUrl = storeImage(profilePicture);
      artisanProfile.setProfilePicture(profilePictureUrl);
    }

    if (storeBanner != null && !storeBanner.isEmpty()) {
      String storeBannerUrl = storeImage(storeBanner);
      artisanProfile.setStoreBanner(storeBannerUrl);
    }

    if (galleryImages != null && !galleryImages.isEmpty()) {
      List<String> galleryUrls = new ArrayList<>();
      for (MultipartFile image : galleryImages) {
        String imageUrl = storeImage(image);
        galleryUrls.add(imageUrl);
      }
      artisanProfile.setGallery(galleryUrls);
    }

    // Update the artisan profile for the current user
    ArtisanProfile updatedProfile = artisanProfileService.updateArtisanProfile(
      artisanProfile,
      currentUser
    );

    // Return the updated profile
    return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteArtisanProfile(@PathVariable Integer id) {
    artisanProfileService.deleteArtisanProfile(id);
    return ResponseEntity.ok().build();
  }

  // Additional endpoint methods can be added here

  private String storeImage(MultipartFile image) throws IOException {
    if (image != null && !image.isEmpty()) {
      Path uploadDir = Paths.get("uploads");

      // If the directory doesn't exist, create it
      if (!Files.exists(uploadDir)) {
        Files.createDirectories(uploadDir);
      }

      // Generate a unique filename using the current time and the original filename
      String filename =
        System.currentTimeMillis() + "_" + image.getOriginalFilename();

      // Save the image file to the upload directory
      Files.copy(image.getInputStream(), uploadDir.resolve(filename));

      // Return the URL of the image
      return "/uploads/" + filename;
    }
    return null;
  }
}
