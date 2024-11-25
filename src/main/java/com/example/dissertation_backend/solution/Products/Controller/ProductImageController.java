package com.example.dissertation_backend.solution.Products.Controller;

import com.example.dissertation_backend.solution.Exception.ImageStorageException;
import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.Products.Service.ProductImageService;
import com.example.dissertation_backend.solution.Products.Service.ProductServices;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/productImages")
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
public class ProductImageController {

  @Autowired
  private ProductImageService productImageService;

  @Autowired
  private ProductServices productService;

  // @GetMapping("/product/{productId}")
  // public List<ProductImages> getImagesByProductId(@PathVariable Integer
  // productId) {
  // return productImageService.getImagesByProductId(productId);
  // }

  @GetMapping("/{id}")
  public ResponseEntity<ProductImages> getImageById(@PathVariable Integer id) {
    return productImageService
      .getImageById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/{productId}")
  public ProductImages createImage(
    @PathVariable Integer productId,
    @RequestBody ProductImages image
  ) {
    Optional<Products> products = productService.getProductById(productId);
    if (products.isPresent()) {
      image.setProduct(products.get());
    }
    return productImageService.saveImage(image);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteImage(@PathVariable Integer id) {
    productImageService.deleteImage(id);
    return ResponseEntity.ok().build();
  }

  // Additional endpoint methods for image handling can be added here
  // Image Resizing
  public byte[] resizeImage(byte[] imageBytes, int width, int height)
    throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails
      .of(new ByteArrayInputStream(imageBytes))
      .size(width, height)
      .toOutputStream(outputStream);
    return outputStream.toByteArray();
  }

  // Image Format Conversion
  public byte[] convertImageFormat(byte[] imageBytes, String format)
    throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails
      .of(new ByteArrayInputStream(imageBytes))
      .outputFormat(format)
      .toOutputStream(outputStream);
    return outputStream.toByteArray();
  }

  // Image Upload
  public void uploadImage(MultipartFile file) {
    try {
      byte[] resizedImage = resizeImage(file.getBytes(), 100, 100);
      String fileExtension = getFileExtension(file.getOriginalFilename());
      String fileName = UUID.randomUUID().toString() + "." + fileExtension;
      Path destinationFilePath = Paths.get("/uploads/", fileName);

      try (
        FileOutputStream fos = new FileOutputStream(
          destinationFilePath.toFile()
        )
      ) {
        fos.write(resizedImage);
      }
    } catch (IOException ex) {
      throw new ImageStorageException("Error processing image", ex);
    }
  }

  private String getFileExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }
  // Image Compression
}
