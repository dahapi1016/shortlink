package com.hapi.shortlink.project.common.convention.errorcode;

public enum UserErrorCode implements IErrorCode {

    USER_NOT_FOUND("B000200", "用户不存在"),
    USER_NAME_EXIST_ERROR("B000201", "用户名已存在"),
    USER_SAVE_ERROR("B000202","用户保存失败"),
    USER_ALREADY_LOGGED_ERROR("C000201", "用户已经在其它设备登录");

    private final String code;

    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
