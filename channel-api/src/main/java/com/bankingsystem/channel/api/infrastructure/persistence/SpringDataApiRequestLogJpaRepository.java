package com.bankingsystem.channel.api.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataApiRequestLogJpaRepository extends JpaRepository<ApiRequestLog, Long> {
}
