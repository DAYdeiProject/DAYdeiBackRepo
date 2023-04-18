package com.sparta.daydeibackrepo.exception;

import com.sparta.daydeibackrepo.exception.message.ExceptionMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ExceptionMessage exceptionMessage;
}
