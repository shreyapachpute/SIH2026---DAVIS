package com.davis.repository;

import com.davis.model.DiscoveredEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscoveredEntityRepository extends JpaRepository<DiscoveredEntity, Long> {
    List<DiscoveredEntity> findByCaseId(Long caseId);
    List<DiscoveredEntity> findByCaseIdAndType(Long caseId, String type);
}
