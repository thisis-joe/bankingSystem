package com.bankingsystem.common.exception;

public class CurrencyMismatchException extends BankingException {

    public CurrencyMismatchException(String accountCurrencyCode, String requestCurrencyCode) {
        super(
            ErrorCode.CURRENCY_MISMATCH,
            "계좌 통화와 요청 통화가 일치하지 않습니다. accountCurrency=%s, requestCurrency=%s"
                .formatted(accountCurrencyCode, requestCurrencyCode)
        );
    }
}
