package com.davis.repository;

import com.davis.model.StressTestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StressTestRepository extends JpaRepository<StressTestRecord, Long> {
    List<StressTestRecord> findByCaseIdOrderByCreatedAtDesc(Long caseId);
}
