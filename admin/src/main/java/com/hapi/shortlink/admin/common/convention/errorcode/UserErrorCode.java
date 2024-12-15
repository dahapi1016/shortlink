package com.hapi.shortlink.admin.common.convention.errorcode;

public enum UserErrorCode implements IErrorCode{

    USER_NOT_FOUND("B000200", "用户不存在");

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
