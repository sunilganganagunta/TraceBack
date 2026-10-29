package com.traceback.audit;

import com.traceback.common.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String actorId, String actorEmail, String action, String resourceType, String resourceId, Map<String, Object> safeMetadata) {
        AuditLog log = new AuditLog(actorId, actorEmail, action, resourceType, resourceId, safeMetadata);
        auditLogRepository.save(log);
    }

    public PagedResponse<AuditLog> getAuditLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAllByOrderByTimestampDesc(pageable);
        return PagedResponse.from(page);
    }
}
