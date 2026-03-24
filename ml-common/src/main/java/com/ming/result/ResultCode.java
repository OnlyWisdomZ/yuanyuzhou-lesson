package com.ming.result;

import lombok.Getter;

/** @author Ming */
@Getter
public enum ResultCode {
    SUCCESS(1000, "请求成功"),
    SERVER_ERROR(1001, "服务器异常"),
    ILLEGAL_PARAM(1002, "非法参数"),
    REPEAT_RECORD(1003, "记录已存在"),
    TOKEN_EXPIRED(1004, "登录过期"),
    TOKEN_EXPIRING_SOON(1005, "登录即将过期");

    private final int CODE;
    private final String MESSAGE;

    ResultCode(int code, String msg) {
        this.CODE = code;
        this.MESSAGE = msg;
    }
}