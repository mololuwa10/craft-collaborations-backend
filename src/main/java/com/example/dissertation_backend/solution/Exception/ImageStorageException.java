package com.example.dissertation_backend.solution.Exception;

public class ImageStorageException extends RuntimeException {
  public ImageStorageException(String message) {
    super(message);
  }

  public ImageStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
