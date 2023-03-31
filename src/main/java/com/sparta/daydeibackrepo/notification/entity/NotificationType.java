package com.sparta.daydeibackrepo.notification.entity;

import java.util.ArrayList;
import java.util.List;

public enum NotificationType {
    FRIEND_ACCEPT("님이 @ 회원님의 친구 신청을 승인하였습니다."), SUBSCRIBE_ACCEPT("님이 @ 구독하기 시작하였습니다."),
    JOIN_REQUEST("님의 @ 일정 참여 요청이 도착하였습니다."), JOIN_REJECT("님이 @ 회원님의 일정 참여 요청을 거절하였습니다."),
    JOIN_UPDATE_REQUEST("님과의 @ 일정이 수정되었습니다."), JOIN_DELETE_REQUEST("님과의 @ 일정이 삭제되었습니다."),
    FRIEND_REQUEST("님이 @ 회원님께 친구 신청을 보냈습니다."), JOIN_ACCEPT("님이 @ 회원님의 일정 참여 요청을 수락하였습니다."),
    SCHEDULE_NOTIFY(" @ 일정이 한 시간 후에 시작됩니다.");

    private String content;

    NotificationType(String content) {
        this.content = content;
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

}
