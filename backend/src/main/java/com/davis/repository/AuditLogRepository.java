package com.davis.repository;

import com.davis.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByCaseIdOrderByTimestampAsc(Long caseId);
    List<AuditLog> findByCaseIdOrderByTimestampDesc(Long caseId);
}
