package com.example.dissertation_backend.solution.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvice {

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<?> handleMaxSizeException(
    MaxUploadSizeExceededException exc
  ) {
    return ResponseEntity
      .status(HttpStatus.EXPECTATION_FAILED)
      .body("File too large!");
  }
}
