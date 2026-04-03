package com.bankingsystem.transfer.application.command;

import com.bankingsystem.transaction.domain.model.ChannelType;
import java.math.BigDecimal;

public record WithdrawalCommand(
    String accountNo,
    BigDecimal amount,
    String currencyCode,
    String requestId,
    String idempotencyKey,
    ChannelType channelType,
    String description
) {
}
