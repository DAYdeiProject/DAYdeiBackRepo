package com.sparta.daydeibackrepo.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
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

}
