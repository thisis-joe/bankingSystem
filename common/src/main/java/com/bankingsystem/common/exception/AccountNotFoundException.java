package com.bankingsystem.common.exception;

public class AccountNotFoundException extends BankingException {

    public AccountNotFoundException(String accountNo) {
        super(ErrorCode.ACCOUNT_NOT_FOUND, "계좌를 찾을 수 없습니다. accountNo=" + accountNo);
    }
}
