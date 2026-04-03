package com.bankingsystem.channel.security.application.service;

import com.bankingsystem.channel.security.application.model.ResolvedChannelIdentity;
import com.bankingsystem.channel.security.domain.model.ApiClient;
import com.bankingsystem.channel.security.domain.model.ApiClientStatus;
import com.bankingsystem.channel.security.domain.model.Channel;
import com.bankingsystem.channel.security.domain.model.ChannelStatus;
import com.bankingsystem.channel.security.domain.repository.ApiClientRepository;
import com.bankingsystem.channel.security.domain.repository.ChannelRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ChannelIdentityResolver {

    private final ChannelRepository channelRepository;
    private final ApiClientRepository apiClientRepository;

    public ChannelIdentityResolver(
        ChannelRepository channelRepository,
        ApiClientRepository apiClientRepository
    ) {
        this.channelRepository = channelRepository;
        this.apiClientRepository = apiClientRepository;
    }

    public ResolvedChannelIdentity resolve(String channelCode, String clientKey) {
        Channel channel = channelRepository.findByChannelCode(channelCode)
            .filter(foundChannel -> foundChannel.getStatus() == ChannelStatus.ACTIVE)
            .orElseThrow(() -> new IllegalStateException("활성 채널 기준 데이터가 없습니다. channelCode=" + channelCode));

        Long apiClientId = resolveApiClientId(channel, clientKey);
        return new ResolvedChannelIdentity(channel.getChannelId(), apiClientId);
    }

    private Long resolveApiClientId(Channel channel, String clientKey) {
        if (!StringUtils.hasText(clientKey)) {
            return null;
        }

        Optional<ApiClient> apiClient = apiClientRepository.findByClientKey(clientKey)
            .filter(foundClient -> foundClient.getStatus() == ApiClientStatus.ACTIVE)
            .filter(foundClient -> foundClient.getChannel().getChannelId().equals(channel.getChannelId()));

        return apiClient.map(ApiClient::getApiClientId).orElse(null);
    }
}
