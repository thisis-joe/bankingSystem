package com.bankingsystem.channel.security.infrastructure.persistence;

import com.bankingsystem.channel.security.domain.model.ApiClient;
import com.bankingsystem.channel.security.domain.repository.ApiClientRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ApiClientRepositoryImpl implements ApiClientRepository {

    private final SpringDataApiClientJpaRepository apiClientJpaRepository;

    public ApiClientRepositoryImpl(SpringDataApiClientJpaRepository apiClientJpaRepository) {
        this.apiClientJpaRepository = apiClientJpaRepository;
    }

    @Override
    public Optional<ApiClient> findByClientKey(String clientKey) {
        return apiClientJpaRepository.findByClientKey(clientKey);
    }
}
