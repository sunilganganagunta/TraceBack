package com.traceback.notifications;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
@CompoundIndexes({
        @CompoundIndex(name = "user_unread_idx", def = "{'recipientId': 1, 'read': 1, 'createdAt': -1}")
})
public class Notification {

    @Id
    private String id;

    private String recipientId;

    private NotificationType type;

    private String title;

    private String message;

    private String relatedResourceId;

    private String relatedResourceType;

    private boolean read = false;

    @CreatedDate
    private Instant createdAt;

    private Instant readAt;

    public Notification() {}

    public Notification(String recipientId, NotificationType type, String title, String message,
                        String relatedResourceId, String relatedResourceType) {
        this.recipientId = recipientId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedResourceId = relatedResourceId;
        this.relatedResourceType = relatedResourceType;
        this.read = false;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRelatedResourceId() { return relatedResourceId; }
    public void setRelatedResourceId(String relatedResourceId) { this.relatedResourceId = relatedResourceId; }

    public String getRelatedResourceType() { return relatedResourceType; }
    public void setRelatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
}
