package com.example.aifomo.exception;

import com.example.aifomo.common.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBiz(BizException e) {
        return ApiResponse.fail(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError() == null ? "参数错误" : e.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResponse.fail(msg);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleAny(Exception e) {
        return ApiResponse.fail(e.getMessage() == null ? "系统异常" : e.getMessage());
    }
}
