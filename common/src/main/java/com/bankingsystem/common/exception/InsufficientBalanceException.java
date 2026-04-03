package com.bankingsystem.common.exception;

public class InsufficientBalanceException extends BankingException {

    public InsufficientBalanceException() {
        super(ErrorCode.INSUFFICIENT_BALANCE);
    }
}
