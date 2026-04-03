package com.bankingsystem.channel.security.infrastructure.persistence;

import com.bankingsystem.channel.security.domain.model.Channel;
import com.bankingsystem.channel.security.domain.repository.ChannelRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ChannelRepositoryImpl implements ChannelRepository {

    private final SpringDataChannelJpaRepository channelJpaRepository;

    public ChannelRepositoryImpl(SpringDataChannelJpaRepository channelJpaRepository) {
        this.channelJpaRepository = channelJpaRepository;
    }

    @Override
    public Optional<Channel> findByChannelCode(String channelCode) {
        return channelJpaRepository.findByChannelCode(channelCode);
    }
}
