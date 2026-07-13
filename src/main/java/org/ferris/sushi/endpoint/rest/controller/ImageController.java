package org.ferris.sushi.endpoint.rest.controller;

import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.ferris.sushi.repository.model.ImageSubmission;
import org.ferris.sushi.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/images")
  public ResponseEntity<ImageSubmission> submit(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email)
      throws IOException {
    ImageSubmission submission = imageService.submitImage(file, email);
    return ResponseEntity.status(HttpStatus.CREATED).body(submission);
  }

  @GetMapping("/images")
  public List<ImageSubmission> findAll() {
    return imageService.findAll();
  }
}
