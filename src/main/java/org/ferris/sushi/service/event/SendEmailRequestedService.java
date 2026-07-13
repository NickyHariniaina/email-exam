package org.ferris.sushi.service.event;

import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.ferris.sushi.PojaGenerated;
import org.ferris.sushi.endpoint.event.model.SendEmailRequested;
import org.ferris.sushi.mail.Email;
import org.ferris.sushi.mail.Mailer;
import org.springframework.stereotype.Service;

@PojaGenerated
@Service
@AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(SendEmailRequested sendEmailRequested) {
    InternetAddress recipientAddress = new InternetAddress(sendEmailRequested.getTo());
    String body = sendEmailRequested.getBody();
    mailer.accept(
        new Email(recipientAddress, List.of(), List.of(), "REGISTER SUCCESSFUL", body, List.of()));
  }
}
