package com.sparta.daydeibackrepo.notification.entity;

public enum NotificationType {
    FRIEND_ACCEPT("님이 회원님의 친구 신청을 승인하였습니다.", "/api/home/profile/"), SUBSCRIBE_ACCEPT("님이 구독하기 시작하였습니다.", "/api/home/profile/"),
    JOIN_REQUEST("님의 일정 참여 요청이 도착하였습니다.", "/api/posts/"), JOIN_REJECT("님이 회원님의 일정 참여 요청을 거절하였습니다.", "/api/posts/"),
    FRIEND_REQUEST("님이 회원님께 친구 신청을 보냈습니다.", "/api/home/profile/"), JOIN_ACCEPT("님이 회원님의 일정 참여 요청을 수락하였습니다.", "/api/posts/");

    private String content;
    private String url;

    NotificationType(String content, String url) {
        this.content = content;
        this.url = url;
    }

    public String makeContent(String title) {
        return "'" + title + "'" + content;
    }

    public String makeUrl(Long id) {
        return url + id;
    }
}
