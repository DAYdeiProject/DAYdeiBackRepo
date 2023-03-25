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
    @Query("select n from Notification n " + "where n.receiver = :subscriber and " + "n.returnId = :subscribingId and " + " n.notificationType = :notificationType")
    Notification findUserSubscribeNotification(User subscriber, Long subscribingId, NotificationType notificationType);
    @Query("select n from Notification n " + "where n.receiver = :user and " + "n.returnId = :postId and " + " n.notificationType = :notificationType")
    Notification findPostSubscribeNotification(User user, Long postId, NotificationType notificationType);
    @Query("select n from Notification n " + "where n.receiver = :responseUser and " + "n.returnId = :requestUserId and " + " n.notificationType = :notificationType")
    Notification findFriendRequestNotification(User responseUser, Long requestUserId,  NotificationType notificationType);
    @Query("select n from Notification n " + "where n.receiver = :responseUser and " + "n.returnId = :requestUserId and " + " n.notificationType = :notificationType")
    Notification findFriendNotification(User responseUser, Long requestUserId, NotificationType notificationType);
}
