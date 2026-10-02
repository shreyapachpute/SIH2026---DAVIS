package com.davis.repository;

import com.davis.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findByCaseIdOrderByCreatedAtDesc(Long caseId);
    Optional<AnalysisResult> findFirstByCaseIdOrderByCreatedAtDesc(Long caseId);
}
