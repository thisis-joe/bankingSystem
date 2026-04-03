package com.bankingsystem.common.exception;

public class SameAccountTransferException extends BankingException {

    public SameAccountTransferException() {
        super(ErrorCode.SAME_ACCOUNT_TRANSFER);
    }
}
