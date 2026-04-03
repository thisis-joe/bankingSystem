package com.bankingsystem.channel.security.domain.model;

import com.bankingsystem.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "api_client")
public class ApiClient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_client_id")
    private Long apiClientId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(name = "client_key", nullable = false, unique = true, length = 100)
    private String clientKey;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApiClientStatus status;

    protected ApiClient() {
    }

    public ApiClient(Channel channel, String clientKey, String clientName, ApiClientStatus status) {
        this.channel = channel;
        this.clientKey = clientKey;
        this.clientName = clientName;
        this.status = status;
    }

    public Long getApiClientId() {
        return apiClientId;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getClientName() {
        return clientName;
    }

    public ApiClientStatus getStatus() {
        return status;
    }
}
