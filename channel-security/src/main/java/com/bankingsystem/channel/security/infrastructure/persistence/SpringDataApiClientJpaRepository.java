package com.bankingsystem.channel.security.infrastructure.persistence;

import com.bankingsystem.channel.security.domain.model.ApiClient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataApiClientJpaRepository extends JpaRepository<ApiClient, Long> {

    Optional<ApiClient> findByClientKey(String clientKey);
}
