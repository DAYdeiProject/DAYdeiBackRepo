package com.sparta.daydeibackrepo.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "회원가입 완료"),
    EMAIL_CHECK_SUCCESS(HttpStatus.OK,"사용 가능한 이메일입니다."),
    TEMPORARY_PASSWORD_HAS_BEEN_EMAILED(HttpStatus.OK, "임시 비밀번호가 이메일로 전송되었습니다."),
    POST_CREATED_SUCCESS(HttpStatus.CREATED,"일정 작성을 완료하였습니다."),
    POST_DATE_PUT_SUCCESS(HttpStatus.OK,"일정의 일자가 변경되었습니다."),
    POST_DELETE_SUCCESS(HttpStatus.OK,"일정이 삭제되었습니다."),
    CATAGORY_CREATED_SUCCESS(HttpStatus.CREATED, "카테고리 등록 완료"),
    MEMO_POST_SUCCESS(HttpStatus.CREATED,"메모 작성이 완료되었습니다."),
    MEMO_PUT_SUCCESS(HttpStatus.OK,"메모 수정이 완료되었습니다."),
    MEMO_DELETE_SUCCESS(HttpStatus.OK, "메모가 삭제되었습니다."),
    FRIEND_DELETE_SUCCESS(HttpStatus.OK, "친구를 삭제했습니다."),
    FRIEND_REQUEST_CANCEL_SUCCESS(HttpStatus.OK, "친구 신청을 취소하였습니다."),
    FRIEND_REQUEST_REJACT_SUCCESS(HttpStatus.OK, "친구 신청을 거절하였습니다."),
    SUBSCRIBE_PUST_VIEW_SUCCESS(HttpStatus.OK,"구독한 일정을 표시합니다"),
    SUBSCRIBE_NOT_PUST_VIEW_SUCCESS(HttpStatus.OK,"구독한 일정을 표시하지 않습니다"),
    NOTIFICATION_DELETED(HttpStatus.OK,"알림 삭제 완료!");
    private final HttpStatus httpStatus;
    private final String detail;
}
