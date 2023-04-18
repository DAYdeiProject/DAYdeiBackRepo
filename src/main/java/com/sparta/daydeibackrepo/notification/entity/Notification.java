package com.sparta.daydeibackrepo.notification.entity;

import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.util.TimeStamped;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class Notification extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column
    private NotificationContent content;

    @Column
    private Long returnId;

    @Column(nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @Builder
    public Notification(User receiver, NotificationType notificationType, String content, Long returnId, Boolean isRead) {
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.content = new NotificationContent(content);
        this.returnId = returnId;
        this.isRead = isRead;
    }

    public String getContent() {
        return content.getContent();
    }
    public void read() {
        this.isRead = true;
    }
}