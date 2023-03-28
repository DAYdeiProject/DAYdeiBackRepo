package com.sparta.daydeibackrepo.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "회원가입 완료"),
    POST_CREATED_SUCCESS(HttpStatus.CREATED,"일정 작성을 완료하였습니다."),
    LOGIN_SUCCESS(HttpStatus.OK,"로그인이 완료 되었습니다."),
    ACOUNT_CHECK_SUCCESS(HttpStatus.OK,"사용 가능한 아이디입니다."),
    EMAIL_CHECK_SUCCESS(HttpStatus.OK,"사용 가능한 이메일입니다."),
    USER_INFO_SUCCESS(HttpStatus.OK, "유저정보 불러오기 성공"),
    TEMPORARY_PASSWORD_HAS_BEEN_EMAILED(HttpStatus.OK, "임시 비밀번호가 이메일로 전송되었습니다."),

    GOODS_DETAIL_SUCCESS(HttpStatus.OK, "상품 정보 불러오기 성공"),
    GOODS_UPDATE_AMOUNT_SUCCESS(HttpStatus.OK, "상품 수량 추가 성공"),
    GOODS_CATEGORY_LIST_SUCCESS(HttpStatus.OK, "카테고리 상품 리스트 불러오기 성공"),
    GOODS_ALL_CATEGORY_LIST_SUCCESS(HttpStatus.OK, "카테고리 상품 전체 리스트 불러오기 성공"),
    ADD_CART_SUCCESS(HttpStatus.OK, "해당 상품을 장바구니에 넣었습니다."),
    UPDATE_CART_SUCCESS(HttpStatus.OK, "상품 수량 조절에 성공했습니다."),
    DELETE_CART_GOODS_SUCCESS(HttpStatus.OK, "해당 상품을 장바구니에서 삭제하셨습니다."),
    CATEGORY_INDEX_SUCCESS(HttpStatus.OK, "카테고리 목록 불러오기 성공"),

    CART_LIST_SUCCESS(HttpStatus.OK, "장바구니 목록 불러오기 성공"),

    BOARD_GET_SUCCESS(HttpStatus.OK,"게시물 랜덤 보기 완료"),
    GOODS_POST_SUCCESS(HttpStatus.CREATED, "상품 등록 완료"),
    CATAGORY_CREATED_SUCCESS(HttpStatus.CREATED, "카테고리 등록 완료"),
    BOARD_PUT_SUCCESS(HttpStatus.CREATED,"게시물 수정 완료"),
    POST_DATE_PUT_SUCCESS(HttpStatus.CREATED,"일정의 일자가 변경되었습니다."),
    POST_DELETE_SUCCESS(HttpStatus.CREATED,"일정이 삭제되었습니다."),
    BOARD_MY_LIST_GET_SUCCESS(HttpStatus.OK,"나의 게시물 보기 완료"),
    BOARD_DETAIL_GET_SUCCESS(HttpStatus.OK,"상세 게시물 보기 완료"),
    COMMENT_POST_SUCCESS(HttpStatus.CREATED,"댓글 작성 완료"),
    COMMENT_DELETE_SUCCESS(HttpStatus.CREATED, "댓글 삭제 완료"),
    LIKE_POST_SUCCESS(HttpStatus.CREATED, "좋아요 등록 완료"),
    LIKE_DELETE_SUCCESS(HttpStatus.CREATED, "좋아요 취소 완료"),
    BUY_SUCCESS(HttpStatus.OK,"구매 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
