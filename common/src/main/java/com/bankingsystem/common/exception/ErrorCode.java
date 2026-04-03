package com.bankingsystem.common.exception;

public enum ErrorCode {
    ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND", "계좌를 찾을 수 없습니다.", 404),
    ACCOUNT_BALANCE_NOT_FOUND("ACCOUNT_BALANCE_NOT_FOUND", "계좌 잔액 정보를 찾을 수 없습니다.", 404),
    INVALID_AMOUNT("INVALID_AMOUNT", "금액은 0보다 커야 합니다.", 400),
    CURRENCY_MISMATCH("CURRENCY_MISMATCH", "계좌 통화와 요청 통화가 일치하지 않습니다.", 400),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "출금 가능 잔액이 부족합니다.", 409),
    SAME_ACCOUNT_TRANSFER("SAME_ACCOUNT_TRANSFER", "출금 계좌와 입금 계좌는 같을 수 없습니다.", 400),
    VALIDATION_ERROR("VALIDATION_ERROR", "요청값 검증에 실패했습니다.", 400),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", 500);

    private final String code;
    private final String message;
    private final int httpStatus;

    ErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
