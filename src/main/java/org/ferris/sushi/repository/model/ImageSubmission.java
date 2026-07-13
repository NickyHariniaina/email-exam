package org.ferris.sushi.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "image_submission")
@Getter
@Setter
public class ImageSubmission {
  @Id private String id;
  private String fileName;
  private String email;
  @CreationTimestamp private Instant createdAt;
}
