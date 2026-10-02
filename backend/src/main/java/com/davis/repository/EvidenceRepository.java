package com.davis.repository;

import com.davis.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByCaseId(Long caseId);
    List<Evidence> findByCaseIdOrderByTimestampAsc(Long caseId);
    List<Evidence> findByCaseIdAndSupportsIsNotNull(Long caseId);
    List<Evidence> findByCaseIdAndContradictsIsNotNull(Long caseId);
}
