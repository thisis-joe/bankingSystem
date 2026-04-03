package com.bankingsystem.transfer.application.command;

import com.bankingsystem.transaction.domain.model.ChannelType;
import java.math.BigDecimal;

public record TransferCommand(
    String sourceAccountNo,
    String destinationAccountNo,
    BigDecimal amount,
    String currencyCode,
    String requestId,
    String idempotencyKey,
    ChannelType channelType,
    String description
) {
}
