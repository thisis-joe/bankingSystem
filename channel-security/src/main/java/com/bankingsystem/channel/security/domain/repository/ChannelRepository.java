package com.bankingsystem.channel.security.domain.repository;

import com.bankingsystem.channel.security.domain.model.Channel;
import java.util.Optional;

public interface ChannelRepository {

    Optional<Channel> findByChannelCode(String channelCode);
}
