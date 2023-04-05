package com.sparta.daydeibackrepo.util;

import com.sparta.daydeibackrepo.exception.message.ExceptionMessage;
import com.sparta.daydeibackrepo.exception.message.SuccessMessage;
import com.sun.net.httpserver.Authenticator;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@NoArgsConstructor
public class StatusResponseDto<T> {

    private int statusCode;
    private T data;

    public StatusResponseDto(int statusCode, T data){
        this.statusCode = statusCode;
        this.data = data;
    }

    public static <T> StatusResponseDto<T> success(T data){
        return new StatusResponseDto<>(HttpStatus.OK.value(), data);
    }

    public static <T> StatusResponseDto<T> fail(HttpStatus httpStatus, T data){
        return new StatusResponseDto<>(httpStatus.value(), data);
    }

    public static ResponseEntity<StatusResponseDto> toExceptionResponseEntity(ExceptionMessage exceptionMessage) {
        return ResponseEntity
                .status(exceptionMessage.getHttpStatus())
                .body(StatusResponseDto.builder()
                        .statusCode(exceptionMessage.getHttpStatus().value())
                        .data(exceptionMessage.getDetail())
                        .build()
                );
    }

    public static StatusResponseDto<?> toResponseEntity(SuccessMessage message) {
        return StatusResponseDto.builder()
                    .statusCode(HttpStatus.OK.value())
                    .data(message.getDetail())
                    .build();
    }

    public static <T> StatusResponseDto<?> toAlldataResponseEntity(T data) {
        return StatusResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .data(data)
                .build();
    }


    public static StatusResponseDto<?> toAllExceptionResponseEntity(ExceptionMessage exceptionMessage) {
        return StatusResponseDto.builder()
                    .statusCode(exceptionMessage.getHttpStatus().value())
                    .data(exceptionMessage.getDetail())
                    .build();
    }

    public static StatusResponseDto<?> toAllExceptionResponseEntity(HttpStatus httpStatus,String message) {
        return StatusResponseDto.builder()
                    .statusCode(httpStatus.value())
                    .data(message)
                    .build();
    }
}
