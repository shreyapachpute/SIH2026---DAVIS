package com.davis.repository;

import com.davis.model.ReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewRecord, Long> {
    List<ReviewRecord> findByCaseIdOrderByReviewedAtDesc(Long caseId);
    Optional<ReviewRecord> findFirstByCaseIdOrderByReviewedAtDesc(Long caseId);
}
