package com.bankingsystem.channel.api.presentation.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferRequest(
    @NotBlank
    String sourceAccountNo,

    @NotBlank
    String destinationAccountNo,

    @NotNull
    @DecimalMin(value = "0.01")
    BigDecimal amount,

    @NotBlank
    String currencyCode,

    String description
) {
}
