package com.bankingsystem.channel.security.domain.repository;

import com.bankingsystem.channel.security.domain.model.ApiClient;
import java.util.Optional;

public interface ApiClientRepository {

    Optional<ApiClient> findByClientKey(String clientKey);
}
