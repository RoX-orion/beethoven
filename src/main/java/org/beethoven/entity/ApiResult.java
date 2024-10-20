package org.beethoven.entity;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-20
 */

@Data
public class ApiResult<T> {

    private Integer code;

    private String msg;

    private T data;


    private ApiResult() {
    }

    public ApiResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ApiResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //成功
    public static <T> ApiResult<T> ok() {
        return new ApiResult<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), null);
    }

    //成功
    public static <T> ApiResult<T> ok(T data){
        return new ApiResult<>(HttpStatus.OK.value(), "OK", data);
    }

    //登录过期
    public static <T> ApiResult<T> expired(String message) {
        return new ApiResult<>(HttpStatus.UNAUTHORIZED.value(), message, null);
    }

    //失败
    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    //失败-自定义状态码
    public static <T> ApiResult<T> fail(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }

    //服务器内部错误
    public static <T> ApiResult<T> error() {
        return new ApiResult<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

//    public static ApiResult unAuthorized() {
//        return new ApiResult(ResponseCode.UNAUTHORIZED.getCode(), ResponseCode.UNAUTHORIZED.getDesc());
//    }

    public static <T> ApiResult<T> build(Integer code, String msg) {
        return new ApiResult<>(code, msg, null);
    }
}
