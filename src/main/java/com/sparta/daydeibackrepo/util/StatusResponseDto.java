package com.sparta.daydeibackrepo.util;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    public static ResponseEntity<StatusResponseDto> toResponseEntity(String message) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StatusResponseDto.builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(message)
                        .build()
                );
    }


    public static ResponseEntity<StatusResponseDto> toAllExceptionResponseEntity(String message) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StatusResponseDto.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .data(message)
                        .build()
                );
    }
}
