package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.NotificationDTO;
import com.EmpTimeHub.entity.User;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    /**
     * Sends a notification to the specified user.
     *
     * @param user        the recipient user
     * @param message     the notification message
     * @param referenceId the reference ID (e.g., leaveId or related entity)
     * @return the created {@link NotificationDTO} with details
     */
    NotificationDTO sendNotification(User user, String message, UUID referenceId);

    /**
     * Sends a notification to the specified user.
     *
     * @param user        the recipient user
     * @param message     the notification message
     * @param referenceId the reference ID (e.g., leaveId or related entity)
     * @return the created {@link NotificationDTO} with details
     */
    List<NotificationDTO> sendNotificationToManager(User user, String message, List<UUID> referenceId);

    /**
     * Retrieves all notifications for the given user, ordered by creation time descending.
     *
     * @param user the user whose notifications are to be fetched
     * @return a list of {@link NotificationDTO} objects
     */
    List<NotificationDTO> getUserNotifications(User user);

    /**
     * Marks a specific notification as read.
     *
     * @param notificationIds the IDs or ID of the notification to mark as read
     */
    void markAsRead(List<UUID> notificationIds);

    /**
     * Deletes all notifications for the specified user.
     *
     * @param user the user whose notifications should be cleared
     */
    void clearAllNotifications(User user);

    /**
     * Deletes a specific notification for the given user.
     * Only notifications belonging to the user will be removed.
     *
     * @param user            the owner of the notification
     * @param notificationIds the IDs or ID of the notification to delete
     */
    void clearNotification(User user, List<UUID> notificationIds);

}
