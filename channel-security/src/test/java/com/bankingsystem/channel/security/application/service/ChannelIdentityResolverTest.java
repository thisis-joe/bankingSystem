package com.bankingsystem.channel.security.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.bankingsystem.channel.security.application.model.ResolvedChannelIdentity;
import com.bankingsystem.channel.security.domain.model.ApiClient;
import com.bankingsystem.channel.security.domain.model.ApiClientStatus;
import com.bankingsystem.channel.security.domain.model.Channel;
import com.bankingsystem.channel.security.domain.model.ChannelCategory;
import com.bankingsystem.channel.security.domain.model.ChannelStatus;
import com.bankingsystem.channel.security.domain.repository.ApiClientRepository;
import com.bankingsystem.channel.security.domain.repository.ChannelRepository;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChannelIdentityResolverTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ApiClientRepository apiClientRepository;

    private ChannelIdentityResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new ChannelIdentityResolver(channelRepository, apiClientRepository);
    }

    @Test
    void 활성_채널과_활성_클라이언트가_있으면_둘다_반환한다() throws Exception {
        Channel channel = new Channel("OPEN_API", "오픈 API", ChannelCategory.EXTERNAL_API, ChannelStatus.ACTIVE);
        setField(channel, "channelId", 1L);

        ApiClient client = new ApiClient(channel, "client-a", "기본 클라이언트", ApiClientStatus.ACTIVE);
        setField(client, "apiClientId", 10L);

        when(channelRepository.findByChannelCode("OPEN_API")).thenReturn(Optional.of(channel));
        when(apiClientRepository.findByClientKey("client-a")).thenReturn(Optional.of(client));

        ResolvedChannelIdentity resolved = resolver.resolve("OPEN_API", "client-a");

        assertThat(resolved.channelId()).isEqualTo(1L);
        assertThat(resolved.apiClientId()).isEqualTo(10L);
    }

    @Test
    void 클라이언트_키가_없으면_채널만_반환한다() throws Exception {
        Channel channel = new Channel("OPEN_API", "오픈 API", ChannelCategory.EXTERNAL_API, ChannelStatus.ACTIVE);
        setField(channel, "channelId", 1L);

        when(channelRepository.findByChannelCode("OPEN_API")).thenReturn(Optional.of(channel));

        ResolvedChannelIdentity resolved = resolver.resolve("OPEN_API", null);

        assertThat(resolved.channelId()).isEqualTo(1L);
        assertThat(resolved.apiClientId()).isNull();
    }

    @Test
    void 활성_채널이_없으면_예외가_발생한다() {
        when(channelRepository.findByChannelCode("OPEN_API")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.resolve("OPEN_API", "client-a"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("활성 채널 기준 데이터가 없습니다");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
