package com.davis.service;

import com.davis.model.AuditLog;
import com.davis.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final IntegrityService integrityService;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository, IntegrityService integrityService) {
        this.auditLogRepository = auditLogRepository;
        this.integrityService = integrityService;
    }

    public AuditLog logAction(Long caseId, String action, String actor, String details) {
        LocalDateTime now = LocalDateTime.now();
        String payloadToHash = caseId + "|" + action + "|" + actor + "|" + now + "|" + details;
        String hash = integrityService.computeSha256(payloadToHash);

        AuditLog log = new AuditLog(caseId, action, actor, now, details, hash);
        return auditLogRepository.save(log);
    }

    public List<AuditLog> getAuditLogs(Long caseId) {
        return auditLogRepository.findByCaseIdOrderByTimestampDesc(caseId);
    }
}
