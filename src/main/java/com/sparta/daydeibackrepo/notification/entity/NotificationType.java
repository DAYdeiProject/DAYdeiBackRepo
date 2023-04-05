package com.sparta.daydeibackrepo.notification.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum NotificationType {
    FRIEND_ACCEPT("님이 @ 회원님의 친구 신청을 승인하였습니다.", "친구알림"), SUBSCRIBE_ACCEPT("님이 @ 구독하기 시작하였습니다.", "구독알림"),
    JOIN_REQUEST("님의 @ 일정 참여 요청이 도착하였습니다.", "일정초대"), JOIN_REJECT("님이 @ 회원님의 일정 참여 요청을 거절하였습니다.", "일정알림"),
    JOIN_UPDATE_REQUEST("님과의 @ 일정이 수정되었습니다.", "일정알림"), JOIN_DELETE_REQUEST("님과의 @ 일정이 삭제되었습니다.", "일정알림"),
    FRIEND_REQUEST("님이 @ 회원님께 친구 신청을 보냈습니다.", "친구신청"), JOIN_ACCEPT("님이 @ 회원님의 일정 참여 요청을 수락하였습니다.", "일정알림"),
    SCHEDULE_NOTIFY(" @ 일정이 한 시간 후에 시작됩니다.", "일정알림");

    private String content;
    private String contentType;

    NotificationType(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public String makeContent(String title) {
        return "'" + title + "'" + content;
    }

    public static List<NotificationType> userContent() {
        List<NotificationType> notificationTypes = new ArrayList<>();
        notificationTypes.add(FRIEND_ACCEPT);
        notificationTypes.add(SUBSCRIBE_ACCEPT);
        notificationTypes.add(FRIEND_REQUEST);
        return notificationTypes;
    }

    public static String getContentType(Notification notification) {
        return notification.getNotificationType().contentType;
    }


}
