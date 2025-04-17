package com.leon.common.exception;

import lombok.Generated;

public class BizException extends RuntimeException {

    private final String retCode;

    public BizException(String retCode, String message) {
        super(message);
        this.retCode = retCode;
    }

    public BizException(String retCode, String message, Throwable cause) {
        super(message, cause);
        this.retCode = retCode;
    }

    @Generated
    public String getRetCode() {
        return this.retCode;
    }
}