package com.bankingsystem.channel.api.presentation.controller;

import com.bankingsystem.channel.api.presentation.request.DepositRequest;
import com.bankingsystem.channel.api.presentation.request.TransferRequest;
import com.bankingsystem.channel.api.presentation.request.WithdrawalRequest;
import com.bankingsystem.channel.api.presentation.response.TransferResponse;
import com.bankingsystem.transaction.domain.model.ChannelType;
import com.bankingsystem.transfer.application.command.DepositCommand;
import com.bankingsystem.transfer.application.command.TransferCommand;
import com.bankingsystem.transfer.application.command.WithdrawalCommand;
import com.bankingsystem.transfer.application.result.TransferResult;
import com.bankingsystem.transfer.application.service.TransferApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransferApplicationService transferApplicationService;

    public TransactionController(TransferApplicationService transferApplicationService) {
        this.transferApplicationService = transferApplicationService;
    }

    @PostMapping("/accounts/{accountNo}/deposit")
    public TransferResponse deposit(
        @PathVariable String accountNo,
        @RequestHeader("X-Request-Id") String requestId,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
        @Valid @RequestBody DepositRequest request
    ) {
        TransferResult result = transferApplicationService.deposit(new DepositCommand(
            accountNo,
            request.amount(),
            request.currencyCode(),
            requestId,
            idempotencyKey,
            ChannelType.OPEN_API,
            request.description()
        ));
        return TransferResponse.from(result);
    }

    @PostMapping("/accounts/{accountNo}/withdraw")
    public TransferResponse withdraw(
        @PathVariable String accountNo,
        @RequestHeader("X-Request-Id") String requestId,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
        @Valid @RequestBody WithdrawalRequest request
    ) {
        TransferResult result = transferApplicationService.withdraw(new WithdrawalCommand(
            accountNo,
            request.amount(),
            request.currencyCode(),
            requestId,
            idempotencyKey,
            ChannelType.OPEN_API,
            request.description()
        ));
        return TransferResponse.from(result);
    }

    @PostMapping("/transfer")
    public TransferResponse transfer(
        @RequestHeader("X-Request-Id") String requestId,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
        @Valid @RequestBody TransferRequest request
    ) {
        TransferResult result = transferApplicationService.transfer(new TransferCommand(
            request.sourceAccountNo(),
            request.destinationAccountNo(),
            request.amount(),
            request.currencyCode(),
            requestId,
            idempotencyKey,
            ChannelType.OPEN_API,
            request.description()
        ));
        return TransferResponse.from(result);
    }
}
