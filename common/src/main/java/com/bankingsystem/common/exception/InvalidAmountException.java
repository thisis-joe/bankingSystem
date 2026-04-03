package com.bankingsystem.common.exception;

public class InvalidAmountException extends BankingException {

    public InvalidAmountException() {
        super(ErrorCode.INVALID_AMOUNT);
    }
}
