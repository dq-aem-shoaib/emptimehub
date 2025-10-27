package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.Notification;
import com.EmpTimeHub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT n FROM Notification n WHERE n.message = :message AND n.createdAt = :createAt")
    List<Notification> getNotificationsByMessageAndByCreateAt(@Param("message") String message,
                                                              @Param("createAt") LocalDate createAt);

}
