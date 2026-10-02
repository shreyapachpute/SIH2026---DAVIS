package com.davis.repository;

import com.davis.model.Indicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndicatorRepository extends JpaRepository<Indicator, Long> {

    List<Indicator> findByCaseId(Long caseId);

    List<Indicator> findByCaseIdOrderByCreatedAtDesc(Long caseId);
}