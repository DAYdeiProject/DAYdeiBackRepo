package com.sparta.daydeibackrepo.notification.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationContent {

    private static final int MAX_LENGTH = 50;

    @Column(nullable = false, length = MAX_LENGTH)
    private String content;

    public NotificationContent(String content) {
        if (isNotValidNotificationContent(content)) {
            throw new IllegalArgumentException(); //InvalidNotificationContentException
        }
        this.content = content;
    }

    private boolean isNotValidNotificationContent(String content) {
        return Objects.isNull(content) || content.isBlank() ||
                content.length() > MAX_LENGTH;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationContent)) return false;
        NotificationContent that = (NotificationContent) o;
        return getContent().equals(that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent());
    }
}