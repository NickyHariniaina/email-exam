package org.ferris.sushi.service;

import static java.io.File.createTempFile;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ferris.sushi.endpoint.event.EventProducer;
import org.ferris.sushi.endpoint.event.model.ImageProcessingRequested;
import org.ferris.sushi.file.bucket.BucketComponent;
import org.ferris.sushi.repository.ImageSubmissionRepository;
import org.ferris.sushi.repository.model.ImageSubmission;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Slf4j
public class ImageService {

  private final ImageSubmissionRepository imageSubmissionRepository;
  private final BucketComponent bucketComponent;
  private final EventProducer<ImageProcessingRequested> eventProducer;

  public ImageSubmission submit(MultipartFile file, String email) throws IOException {
    validateImage(file);

    String id = UUID.randomUUID().toString();
    String originalFilename = file.getOriginalFilename();
    String extension = extractExtension(originalFilename);

    ImageSubmission submission = new ImageSubmission();
    submission.setId(id);
    submission.setFileName(originalFilename);
    submission.setEmail(email);
    imageSubmissionRepository.save(submission);

    File tempFile = createTempFile(id, "." + extension);
    file.transferTo(tempFile);

    File bwFile = convertToBlackAndWhite(tempFile, extension);
    String bucketKey = "images/" + id + "_bw." + extension;
    bucketComponent.upload(bwFile, bucketKey);

    tempFile.delete();
    bwFile.delete();

    ImageProcessingRequested event =
        ImageProcessingRequested.builder().submissionId(id).email(email).s3Key(bucketKey).build();
    eventProducer.accept(List.of(event));

    log.info("Image submitted: id={}, fileName={}, email={}", id, originalFilename, email);
    return submission;
  }

  public List<ImageSubmission> getAll() {
    return imageSubmissionRepository.findAll();
  }

  private void validateImage(MultipartFile file) {
    String contentType = file.getContentType();
    if (contentType == null
        || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
      throw new IllegalArgumentException("Only JPEG and PNG images are accepted");
    }
  }

  private File convertToBlackAndWhite(File original, String formatName) throws IOException {
    BufferedImage originalImage = ImageIO.read(new FileInputStream(original));
    BufferedImage resized = resizeImage(originalImage, 1024);
    BufferedImage bwImage =
        new BufferedImage(
            resized.getWidth(), resized.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bwImage.createGraphics();
    graphics.drawImage(resized, 0, 0, null);
    graphics.dispose();

    File bwFile = createTempFile("bw-", "." + formatName);
    ImageIO.write(bwImage, formatName, new FileOutputStream(bwFile));
    return bwFile;
  }

  private BufferedImage resizeImage(BufferedImage original, int maxSize) {
    int width = original.getWidth();
    int height = original.getHeight();
    if (width <= maxSize && height <= maxSize) {
      return original;
    }
    double scale = Math.min((double) maxSize / width, (double) maxSize / height);
    int newWidth = (int) (width * scale);
    int newHeight = (int) (height * scale);
    BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = resized.createGraphics();
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(original, 0, 0, newWidth, newHeight, null);
    g.dispose();
    return resized;
  }

  private String extractExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "jpg";
    }
    return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
  }
}
