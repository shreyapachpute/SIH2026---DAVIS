package com.davis.repository;

import com.davis.model.TextSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextSampleRepository extends JpaRepository<TextSample, Long> {
    List<TextSample> findByCaseId(Long caseId);
}
