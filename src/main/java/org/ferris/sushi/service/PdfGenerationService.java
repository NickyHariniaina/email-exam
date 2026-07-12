package org.ferris.sushi.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {

  private final SpringTemplateEngine templateEngine;

  public byte[] generatePdf(String templateName, Map<String, Object> variables) {
    var context = new Context();
    context.setVariables(variables);

    String html = templateEngine.process(templateName, context);

    try (var os = new ByteArrayOutputStream()) {
      var builder = new PdfRendererBuilder();
      builder.withHtmlContent(html, null);
      builder.toStream(os);
      builder.run();
      return os.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate PDF from template: " + templateName, e);
    }
  }
}
