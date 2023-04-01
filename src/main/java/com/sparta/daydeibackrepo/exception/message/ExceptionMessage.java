package com.sparta.daydeibackrepo.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
@AllArgsConstructor
public enum ExceptionMessage {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    TIME_SETTING_IS_INCORRECT(BAD_REQUEST,"일정의 시간 설정이 올바르지 않습니다."),
    START_DATE_MUST_BE_EARLY_END_DATE(BAD_REQUEST,"일정의 시간 설정이 올바르지 않습니다."),
    CANNOT_FOLLOW_MYSELF(BAD_REQUEST, "자기 자신은 팔로우 할 수 없습니다"),
    IMAGE_INVALID(BAD_REQUEST,"이미지가 잘못 되었습니다."),
    PASSWORD_INCORRECT_MISMATCH(BAD_REQUEST,"비밀번호가 일치하지 않습니다."),
    PASSWORD_INCORRECT(BAD_REQUEST,"비밀번호가 옳지 않습니다."),
    BIRTHDAY_INCORRECT(BAD_REQUEST,"생일이 일치하지 않습니다."),
    INVALID_FRIEND_REQUEST(BAD_REQUEST,"올바르지 않은 친구 요청입니다."),
    INVALID_FRIEND_DELETE_REQUEST(BAD_REQUEST,"삭제 요청이 올바르지 않습니다."),
    ALREADY_FRIEND_OR_HAVE_UNPROCESSED_FRIEND_REQUEST(BAD_REQUEST,"이미 친구 상태이거나 처리 되지 않은 친구 신청이 있습니다"),
    FRIEND_STATUS_INCORRECT(BAD_REQUEST,"친구 상태가 올바르지 않습니다."),
    INVALID_REQUEST(BAD_REQUEST,"올바르지 않은 요청입니다."),
    INVALID_SHARED_POST_CREATE(BAD_REQUEST,"올바르지 않은 공유일정 생성입니다."),
    INVALID_SHARED_POST_MODIFY(BAD_REQUEST,"올바르지 않은 공유일정 수정입니다."),
    INVALID_SHARED_POST_DELETE(BAD_REQUEST,"올바르지 않은 공유일정 삭제입니다."),
    INVALID_SUBSCRIBE_REQUEST(BAD_REQUEST,"구독 요청이 올바르지 않습니다."),
    INVALID_SUBSCRIBE_CANCEL(BAD_REQUEST,"구독 취소 요청이 올바르지 않습니다."),


    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "인증된 사용자가 아닙니다."),
    UNAUTHORIZED_UPDATE_OR_DELETE(UNAUTHORIZED,"작성자만 수정/삭제할 수 있습니다."),
    NOT_LOGGED_ID(UNAUTHORIZED, "로그인이 되어있지 않습니다."),
    NOT_SUBSCRIBE_USER(UNAUTHORIZED,"구독하지 않은 계정입니다"),


    /* 403 FORBIDDEN : 권한 없음 */
    USER_FORBIDDEN(FORBIDDEN, "권한이 없습니다."),
    POST_VIEW_ONLY_CREATOR_FORBIDDEN(FORBIDDEN,"작성자가 나만 보기로 설정한 일정입니다."),
    POST_VIEW_ONLY_FRIEND_FORBIDDEN(FORBIDDEN,"작성자가 친구공개로 설정한 일정입니다"),


    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    USER_NOT_FOUND(NOT_FOUND, "사용자가 존재하지 않습니다."),
    USER_NOT_VIEW(NOT_FOUND, "유저를 조회할 수 없습니다."),
    POST_NOT_FOUND(NOT_FOUND, "존재하지 않는 게시물입니다."),
    MEMO_NOT_FOUND(NOT_FOUND, "존재하지 않는 메모입니다."),
    NO_ACCEPTABLE_FRIEND_REQUEST(NOT_FOUND,"승인 가능한 친구 요청이 없습니다."),
    NO_APPROVE_POST_JOIN_REQUEST(NOT_FOUND,"수락 가능한 공유 일정이 없습니다."),
    NO_REJACT_POST_JOIN_REQUEST(NOT_FOUND,"수락 가능한 공유 일정이 없습니다."),


    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_USER(CONFLICT,"이미 가입된 사용자입니다."),
    DUPLICATE_EMAIL(CONFLICT,"중복된 이메일입니다."),
    DUPLICATE_CATEGORY(CONFLICT,"이미 등록된 카테고리입니다."),
    DUPLICATE_TAG_USER_JOIN_POST(CONFLICT,"해당 유저는 이미 일정 초대되었습니다."),
    DUPLICATE_SUBSCRIBE_USER(CONFLICT,"이미 구독하고 있는 유저입니다.");


    private final HttpStatus httpStatus;
    private final String detail;
}
