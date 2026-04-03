package com.bankingsystem.channel.api.application.service;

import com.bankingsystem.channel.api.application.command.ApiRequestLogCommand;
import com.bankingsystem.channel.api.infrastructure.persistence.ApiRequestLog;
import com.bankingsystem.channel.api.infrastructure.persistence.SpringDataApiRequestLogJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiRequestLogService {

    private final SpringDataApiRequestLogJpaRepository apiRequestLogJpaRepository;

    public ApiRequestLogService(SpringDataApiRequestLogJpaRepository apiRequestLogJpaRepository) {
        this.apiRequestLogJpaRepository = apiRequestLogJpaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(ApiRequestLogCommand command) {
        apiRequestLogJpaRepository.save(ApiRequestLog.from(command));
    }
}
