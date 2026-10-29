package com.traceback.audit;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "audit_logs")
@CompoundIndexes({
        @CompoundIndex(name = "resource_audit_idx", def = "{'resourceType': 1, 'resourceId': 1}"),
        @CompoundIndex(name = "timestamp_audit_idx", def = "{'timestamp': -1}")
})
public class AuditLog {

    @Id
    private String id;

    private String actorId;

    private String actorEmail;

    private String action;

    private String resourceType;

    private String resourceId;

    @CreatedDate
    private Instant timestamp;

    private Map<String, Object> safeMetadata = new HashMap<>();

    public AuditLog() {}

    public AuditLog(String actorId, String actorEmail, String action, String resourceType, String resourceId, Map<String, Object> safeMetadata) {
        this.actorId = actorId;
        this.actorEmail = actorEmail;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.safeMetadata = safeMetadata != null ? safeMetadata : new HashMap<>();
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getSafeMetadata() { return safeMetadata; }
    public void setSafeMetadata(Map<String, Object> safeMetadata) { this.safeMetadata = safeMetadata; }
}
