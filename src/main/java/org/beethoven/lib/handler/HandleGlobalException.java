package org.beethoven.lib.handler;

import lombok.extern.slf4j.Slf4j;
import org.beethoven.lib.exception.AuthenticationException;
import org.beethoven.lib.exception.BeethovenException;
import org.beethoven.lib.exception.MediaException;
import org.beethoven.pojo.entity.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-28
 */

@Slf4j
@RestControllerAdvice
public class HandleGlobalException {

//    @ResponseBody
//    @ExceptionHandler(NotLoginException.class)
//    public ApiResult notLoginException(NotLoginException notLoginException) {
//        return ApiResult.expired(notLoginException.getMessage());
//    }
//
//    @ResponseBody
//    @ExceptionHandler(RequestIncompleteException.class)
//    public ApiResult notLoginException(RequestIncompleteException e) {
//        return ApiResult.expired(e.getMessage());
//    }
//
//    @ResponseBody
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ApiResult error(HttpMessageNotReadableException e) {
//        e.printStackTrace();
//        return ApiResult.build(500, "请求参数异常！");
//    }
//
    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ApiResult<String> error(AuthenticationException e) {
        log.error(e.getMessage());
        return ApiResult.expired(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<String> error(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error(errors.toString());
        return ApiResult.build(HttpStatus.BAD_REQUEST.value(), errors.toString());
    }

    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult<String> error(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage());
        return ApiResult.build(400, "请求方法错误！");
    }

    @ResponseBody
    @ExceptionHandler({BeethovenException.class, MediaException.class})
    public ApiResult<String> handleBeethovenException(Exception e) {
        e.printStackTrace();
        return ApiResult.build(400, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ApiResult<String> error(Exception e) {
        e.printStackTrace();
        return ApiResult.error();
    }
}
