package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.NotificationDTO;
import com.EmpTimeHub.entity.Notification;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.repository.NotificationRepository;
import com.EmpTimeHub.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing user notifications.
 * Handles persistence and WebSocket-based real-time delivery.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Persist and broadcast new notifications.</li>
 *   <li>Retrieve user-specific notifications in reverse chronological order.</li>
 *   <li>Mark notifications as read and clear user notifications.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Sends a new notification to a specific user.
     * <p>Persists the notification, then broadcasts it to the user in real-time via WebSocket.</p>
     *
     * @param user        the recipient of the notification
     * @param message     the notification message
     * @param referenceId the reference entity ID (e.g., leave request, task ID)
     * @return the persisted notification as a DTO
     */
    @Override
    @Transactional
    public NotificationDTO sendNotification(User user, String message, UUID referenceId) {
        log.info(" Creating notification for userId={} with referenceId={} | message={}",
                user.getUserId(), referenceId, message);

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .referenceId(referenceId)
                .read(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        NotificationDTO dto = NotificationDTO.builder()
                .id(savedNotification.getId())
                .message(savedNotification.getMessage())
                .referenceId(savedNotification.getReferenceId())
                .read(savedNotification.getRead())
                .createdAt(savedNotification.getCreatedAt())
                .updatedAt(savedNotification.getUpdatedAt())
                .build();

        // Send via WebSocket to the specific user's topic
        String destination = "/topic/notifications/" + user.getUserId();
        log.debug(" Sending WebSocket notification to {}", destination);
        messagingTemplate.convertAndSend(destination, dto);

        return dto;
    }

    /**
     * Sends a new notification to a specific user.
     * <p>Persists the notification, then broadcasts it to the user in real-time via WebSocket.</p>
     *
     * @param user        the recipient of the notification
     * @param message     the notification message
     * @param referenceIds the reference entity ID (e.g., timesheet request, task ID)
     * @return the persisted notification as a DTOs
     */
    @Override
    public List<NotificationDTO> sendNotificationToManager(User user, String message, List<UUID> referenceIds) {
        log.info(" Creating notification for userId={} with referenceIds={} | message={}",
                user.getUserId(), referenceIds, message);

        List<Notification> notifications = referenceIds.stream().map(id -> {
            return Notification.builder()
                    .user(user)
                    .message(message)
                    .referenceId(id)
                    .read(false)
                    .build();
        }).toList();

        List<NotificationDTO> dtos = new ArrayList<>();
        for(Notification notification : notifications) {
            notificationRepository.save(notification);
            dtos.add(NotificationDTO.builder()
                    .id(notification.getId())
                    .message(notification.getMessage())
                    .referenceId(notification.getReferenceId())
                    .read(notification.getRead())
                    .createdAt(notification.getCreatedAt())
                    .updatedAt(notification.getUpdatedAt())
                    .build()
            );
        }

        // Send via WebSocket to the specific user's topic
        String destination = "/topic/notifications/" + user.getUserId();
        log.debug("Sending WebSocket notification to {}", destination);
        messagingTemplate.convertAndSend(destination, dtos);

        return dtos;
    }

    /**
     * Retrieves all notifications for a given user, ordered by creation date (newest first).
     *
     * @param user the user whose notifications should be fetched
     * @return list of notifications in descending order of creation
     */
    @Override
    public List<NotificationDTO> getUserNotifications(User user) {
        log.info(" Fetching all notifications for userId={}", user.getUserId());

        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(notification -> NotificationDTO.builder()
                        .id(notification.getId())
                        .message(notification.getMessage())
                        .referenceId(notification.getReferenceId())
                        .read(notification.getRead())
                        .createdAt(notification.getCreatedAt())
                        .updatedAt(notification.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Marks a given notification as read if not already.
     *
     * @param notificationIds the unique IDs of the notification to mark as read
     */
    @Override
    @Transactional
    public void markAsRead(List<UUID> notificationIds) {
        log.info(" Marking notificationId={} as read", notificationIds);

        notificationIds.forEach(notificationId ->
            notificationRepository.findById(notificationId).ifPresentOrElse(notification -> {
                if (Boolean.TRUE.equals(notification.getRead())) {
                    log.debug("NotificationId={} is already marked as read. Skipping update.", notificationId);
                } else {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                    log.info(" NotificationId={} marked as read successfully", notificationId);
                }
            }, () -> log.warn("️ No notification found with ID={}", notificationId))
        );
    }

    /**
     * clear a specific notification for the given user.
     * <p>
     * Only notifications belonging to the provided user will be deleted.
     * Logs a warning if a user attempts to delete a notification they do not own.
     *
     * @param user           the owner of the notification
     * @param notificationIds the unique IDs of the notification to delete
     */
    @Override
    @Transactional
    public void clearNotification(User user, List<UUID> notificationIds) {
        notificationIds.forEach( notificationId ->
            notificationRepository.findById(notificationId).ifPresent(notification -> {
                if (notification.getUser().getUserId().equals(user.getUserId())) {
                    notificationRepository.delete(notification);
                    log.info("Deleted notificationId={} for userId={}", notificationIds, user.getUserId());
                } else {
                    log.warn("UserId={} attempted to delete notificationId={} not owned by them", user.getUserId(), notificationIds);
                }
            })
        );
    }


    /**
     * Deletes all notifications for a given user.
     *
     * @param user the user whose notifications should be cleared
     */
    @Override
    @Transactional
    public void clearAllNotifications(User user) {
        log.info(" Clearing all notifications for userId={}", user.getUserId());

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        if (notifications.isEmpty()) {
            log.debug("No notifications found for userId={}, nothing to delete.", user.getUserId());
            return;
        }

        notificationRepository.deleteAllInBatch(notifications);
        log.info(" Cleared {} notifications for userId={}", notifications.size(), user.getUserId());
    }
}
