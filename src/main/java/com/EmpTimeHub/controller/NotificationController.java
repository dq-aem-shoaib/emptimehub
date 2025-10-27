package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.NotificationDTO;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.repository.UserRepository;
import com.EmpTimeHub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.EmpTimeHub.constants.EndpointConstants.*;

/**
 * Controller for managing notifications.
 * <p>
 * Uses authenticated UserDetails (email/company mail) to retrieve the User entity.
 * Supports fetching, marking read, and clearing notifications.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Get all notifications for the logged-in user.
     *
     * @param userDetails injected authenticated user details
     * @return list of NotificationDTOs
     */
     @GetMapping(GET_NOTIFICATIONS)
     @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN', 'EMPLOYEE')")
     public ResponseEntity<WebResponseDTO<List<NotificationDTO>>> getUserNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        String companyMail = userDetails.getUsername();
        log.info("Fetching notifications for user: {}", companyMail);

        User user = userRepository.findByCompanyEmail(companyMail)
                .orElseThrow(() -> new IllegalStateException("User not found for email: " + companyMail));

        List<NotificationDTO> notifications = notificationService.getUserNotifications(user);
        log.info("Retrieved {} notifications for user: {}", notifications.size(), companyMail);

        return ResponseEntity.ok(
                WebResponseDTO.<List<NotificationDTO>>builder()
                        .flag(true)
                        .status(200)
                        .message("Notifications fetched successfully")
                        .response(notifications)
                        .totalRecords((long) notifications.size())
                        .build()
        );
     }

    /**
     * Mark a specific notification as read.
     *
     * @param notificationIds IDs of the notification to mark as read
     * @param userDetails    injected authenticated user details
     * @return HTTP 204 No Content
     */
    @PatchMapping(READ_NOTIFICATION)
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN', 'EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<Void>> markAsRead(@RequestParam List<UUID> notificationIds,
                                                           @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        log.info("User {} marking notificationId={} as read", email, notificationIds);

        notificationService.markAsRead(notificationIds);

        log.info("NotificationId={} marked as read for user {}", notificationIds, email);
        return ResponseEntity.ok(
                WebResponseDTO.<Void>builder()
                        .flag(true)
                        .status(200)
                        .message("Notification marked as read successfully")
                        .build()
        );
    }

    /**
    * clear a specific notification for the logged-in user by its ID.
    * Only notifications belonging to the authenticated user can be deleted.
    * Returns HTTP 204 No Content on success.
    * Throws IllegalStateException if the user is not found in the database.
   */

    @DeleteMapping(CLEAR_NOTIFICATION)
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN', 'EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<Void>> clearNotification(@RequestParam List<UUID> notificationIds,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        String companyMail = userDetails.getUsername();
        log.info("User {} deleting notificationId={}", companyMail, notificationIds);

        User user = userRepository.findByCompanyEmail(companyMail)
                .orElseThrow(() -> new IllegalStateException("User not found for email: " + companyMail));

        notificationService.clearNotification(user, notificationIds);

        log.info("NotificationId={} deleted for user {}", notificationIds, companyMail);
        return ResponseEntity.ok(
                WebResponseDTO.<Void>builder()
                        .flag(true)
                        .status(200)
                        .message("Notification cleared successfully")
                        .build()
        );
    }


    /**
     * Clear all notifications for the logged-in user.
     *
     * @param userDetails injected authenticated user details
     * @return HTTP 204 No Content
     */
    @DeleteMapping(CLEAR_ALL_NOTIFICATIONS)
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN', 'EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<Void>> clearAllNotifications(@AuthenticationPrincipal UserDetails userDetails) {

        String companyMail = userDetails.getUsername();
        log.info("Clearing all notifications for user: {}", companyMail);

        User user = userRepository.findByCompanyEmail(companyMail)
                .orElseThrow(() -> new IllegalStateException("User not found for email: " + companyMail));

        notificationService.clearAllNotifications(user);

        log.info("Cleared all notifications for user: {}", companyMail);
        return ResponseEntity.ok(
                WebResponseDTO.<Void>builder()
                        .flag(true)
                        .status(200)
                        .message("All notifications cleared successfully")
                        .build()
        );
    }
}
