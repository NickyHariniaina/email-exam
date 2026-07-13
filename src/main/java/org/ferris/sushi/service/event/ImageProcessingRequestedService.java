package org.ferris.sushi.service.event;

import jakarta.mail.internet.InternetAddress;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ferris.sushi.endpoint.event.model.ImageProcessingRequested;
import org.ferris.sushi.file.bucket.BucketComponent;
import org.ferris.sushi.mail.Email;
import org.ferris.sushi.mail.Mailer;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@AllArgsConstructor
@Slf4j
public class ImageProcessingRequestedService implements Consumer<ImageProcessingRequested> {

  private final BucketComponent bucketComponent;
  private final Mailer mailer;
  private final SpringTemplateEngine templateEngine;

  @SneakyThrows
  @Override
  public void accept(ImageProcessingRequested event) {
    URL presignedUrl = bucketComponent.presign(event.getS3Key(), Duration.ofHours(1));

    String htmlBody = renderEmailTemplate(event.getEmail(), presignedUrl.toString());
    InternetAddress recipientAddress = new InternetAddress(event.getEmail());
    mailer.accept(
        new Email(
            recipientAddress,
            List.of(),
            List.of(),
            "Your image has been processed",
            htmlBody,
            List.of()));

    log.info(
        "Image processed: submissionId={}, email={}", event.getSubmissionId(), event.getEmail());
  }

  private String renderEmailTemplate(String email, String imageUrl) {
    Context context = new Context();
    context.setVariable("email", email);
    context.setVariable("imageUrl", imageUrl);
    return templateEngine.process("image-processed", context);
  }
}
