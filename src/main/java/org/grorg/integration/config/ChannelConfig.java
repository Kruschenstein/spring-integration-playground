package org.grorg.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ChannelConfig {
    @Bean
    public MessageChannel incoming() {
        return MessageChannels.direct().get();
    }

    @Bean
    public TcpNetServerConnectionFactory connectionFactory() {
        return new TcpNetServerConnectionFactory(1234);
    }

    @Bean
    public TcpReceivingChannelAdapter receiver() {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
        adapter.setConnectionFactory(connectionFactory());
        adapter.setOutputChannel(incoming());
        return adapter;
    }

    @Bean
    public TcpSendingMessageHandler sender() {
        TcpSendingMessageHandler handler = new TcpSendingMessageHandler();
        handler.setConnectionFactory(connectionFactory());
        return handler;
    }
}
