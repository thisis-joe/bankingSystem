package com.bankingsystem.channel.security.domain.model;

import com.bankingsystem.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "channel")
public class Channel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "channel_code", nullable = false, unique = true, length = 30)
    private String channelCode;

    @Column(name = "channel_name", nullable = false, length = 100)
    private String channelName;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 30)
    private ChannelCategory channelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ChannelStatus status;

    protected Channel() {
    }

    public Channel(String channelCode, String channelName, ChannelCategory channelType, ChannelStatus status) {
        this.channelCode = channelCode;
        this.channelName = channelName;
        this.channelType = channelType;
        this.status = status;
    }

    public Long getChannelId() {
        return channelId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public ChannelCategory getChannelType() {
        return channelType;
    }

    public ChannelStatus getStatus() {
        return status;
    }
}
