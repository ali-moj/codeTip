package com.jvpars.codetip.config;

import com.jvpars.codetip.utils.MyArgUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Configuration
@Slf4j
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {


    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpDestMatchers("/codeTipSocket/**").authenticated();
        messages.simpSubscribeDestMatchers("/codeTipSocket/**").authenticated();

    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
