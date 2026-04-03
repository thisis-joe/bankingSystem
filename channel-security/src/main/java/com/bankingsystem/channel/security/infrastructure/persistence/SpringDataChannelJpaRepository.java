package com.bankingsystem.channel.security.infrastructure.persistence;

import com.bankingsystem.channel.security.domain.model.Channel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataChannelJpaRepository extends JpaRepository<Channel, Long> {

    Optional<Channel> findByChannelCode(String channelCode);
}
