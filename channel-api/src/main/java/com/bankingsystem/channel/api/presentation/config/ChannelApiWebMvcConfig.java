package com.bankingsystem.channel.api.presentation.config;

import com.bankingsystem.channel.api.presentation.interceptor.ApiRequestLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ChannelApiWebMvcConfig implements WebMvcConfigurer {

    private final ApiRequestLoggingInterceptor apiRequestLoggingInterceptor;

    public ChannelApiWebMvcConfig(ApiRequestLoggingInterceptor apiRequestLoggingInterceptor) {
        this.apiRequestLoggingInterceptor = apiRequestLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiRequestLoggingInterceptor)
            .addPathPatterns("/api/v1/transactions/**");
    }
}
