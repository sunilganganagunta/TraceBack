package com.traceback.notifications;

import com.traceback.common.PagedResponse;
import com.traceback.common.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(String recipientId, NotificationType type, String title,
                                           String message, String resourceId, String resourceType) {
        Notification notification = new Notification(recipientId, type, title, message, resourceId, resourceType);
        return notificationRepository.save(notification);
    }

    public PagedResponse<Notification> getUserNotifications(String recipientId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
        return PagedResponse.from(page);
    }

    public long getUnreadCount(String recipientId) {
        return notificationRepository.countByRecipientIdAndReadFalse(recipientId);
    }

    public Notification markAsRead(String notificationId, String recipientId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getRecipientId().equals(recipientId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }

        notification.setRead(true);
        notification.setReadAt(Instant.now());
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(String recipientId) {
        List<Notification> unread = notificationRepository.findByRecipientIdAndReadFalse(recipientId);
        Instant now = Instant.now();
        for (Notification n : unread) {
            n.setRead(true);
            n.setReadAt(now);
        }
        notificationRepository.saveAll(unread);
    }
}
