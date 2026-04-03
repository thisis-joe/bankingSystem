package com.bankingsystem.channel.api.presentation.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record WithdrawalRequest(
    @NotNull
    @DecimalMin(value = "0.01")
    BigDecimal amount,

    @NotBlank
    String currencyCode,

    String description
) {
}
