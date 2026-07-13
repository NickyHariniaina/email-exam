package org.ferris.sushi.repository;

import org.ferris.sushi.repository.model.ImageSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageSubmissionRepository extends JpaRepository<ImageSubmission, String> {}
