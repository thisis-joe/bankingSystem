package com.bankingsystem.common.exception;

public class AccountBalanceNotFoundException extends BankingException {

    public AccountBalanceNotFoundException(Long accountId) {
        super(ErrorCode.ACCOUNT_BALANCE_NOT_FOUND, "계좌 잔액 정보를 찾을 수 없습니다. accountId=" + accountId);
    }
}
