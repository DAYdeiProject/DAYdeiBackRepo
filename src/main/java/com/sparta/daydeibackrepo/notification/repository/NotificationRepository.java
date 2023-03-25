package com.sparta.daydeibackrepo.notification.repository;

import com.sparta.daydeibackrepo.notification.entity.Notification;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n from Notification n " + "where n.receiver.id = :userId " + "order by n.id desc")
    List<Notification> findAllByUserId(@Param("userId") Long userId);

    @Query("select count(n) from Notification n " + "where n.receiver.id = :userId and " + "n.isRead = false")
    Long countUnReadNotifications(@Param("userId") Long userId);

    @Query("select n from Notification n " + "where n.receiver = :user and " + "n.returnId = :returnId and " + " n.notificationType = :notificationType")
    Notification findNotification(User user, Long returnId,  NotificationType notificationType);}
