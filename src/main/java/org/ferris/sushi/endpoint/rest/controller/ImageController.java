package org.ferris.sushi.endpoint.rest.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.ferris.sushi.endpoint.event.EventProducer;
import org.ferris.sushi.endpoint.event.model.SendEmailRequested;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ImageController {

  private final EventProducer<SendEmailRequested> eventProducer;

  @PostMapping("/images")
  public ResponseEntity<String> submit(@RequestParam String email) {
    var event =
        SendEmailRequested.builder().to(email).body("Your image has been received.").build();
    eventProducer.accept(List.of(event));
    return ResponseEntity.status(HttpStatus.CREATED).body("ok");
  }
}
