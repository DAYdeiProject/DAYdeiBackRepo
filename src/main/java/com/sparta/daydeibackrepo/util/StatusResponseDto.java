package com.sparta.daydeibackrepo.util;

import com.sparta.daydeibackrepo.exception.dto.ExceptionMessage;
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
                        .data(exceptionMessage)
                        .build()
                );
    }

    public static ResponseEntity<StatusResponseDto> toResponseEntity(String message) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StatusResponseDto.builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(message)
                        .build()
                );
    }


    public static ResponseEntity<StatusResponseDto> toAllExceptionResponseEntity(ExceptionMessage exceptionMessage) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StatusResponseDto.builder()
                        .statusCode(exceptionMessage.getHttpStatus().value())
                        .data(exceptionMessage)
                        .build()
                );
    }

    public static ResponseEntity<StatusResponseDto> toAllExceptionResponseEntity(HttpStatus httpStatus,String message) {
        return ResponseEntity
                .status(httpStatus)
                .body(StatusResponseDto.builder()
                        .statusCode(httpStatus.value())
                        .data(message)
                        .build()
                );
    }
}
