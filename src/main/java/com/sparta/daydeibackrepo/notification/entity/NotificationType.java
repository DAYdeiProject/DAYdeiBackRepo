package com.sparta.daydeibackrepo.notification.entity;

public enum NotificationType {
    FRIEND_ACCEPT("님이 친구 신청을 승인하였습니다.", "api/friends/"), SUBSCRIBE_ACCEPT("님이 구독하기 시작하였습니다.", "api/subscribes/"),
    JOIN("님의 일정 참여 요청이 도착하였습니다.", "api/posts/"), REJECT("님이 회원님의 일정 참여 요청을 거절하였습니다.", "api/posts/"),
    CANCEL("님이 회원님의 친구 신청을 거절하였습니다.", "api/friends/");

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
