package com.bankingsystem.channel.security.application.model;

public record ResolvedChannelIdentity(
    Long channelId,
    Long apiClientId
) {
}
