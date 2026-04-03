package com.bankingsystem.common.exception;

public class IdempotencyConflictException extends BankingException {

    public IdempotencyConflictException(String detailMessage) {
        super(ErrorCode.IDEMPOTENCY_CONFLICT, detailMessage);
    }
}
