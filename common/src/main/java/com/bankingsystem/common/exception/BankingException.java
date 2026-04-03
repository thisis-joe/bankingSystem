package com.bankingsystem.common.exception;

public class BankingException extends RuntimeException {

    private final ErrorCode errorCode;

    public BankingException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BankingException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
