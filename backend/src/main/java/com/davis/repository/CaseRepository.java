package com.davis.repository;

import com.davis.model.CaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<CaseEntity, Long> {
    Optional<CaseEntity> findByCaseNumber(String caseNumber);
}
