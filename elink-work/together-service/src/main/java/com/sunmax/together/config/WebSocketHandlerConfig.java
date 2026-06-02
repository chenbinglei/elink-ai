package com.sunmax.together.config;

import com.sunmax.together.websocket.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketHandlerConfig implements WebSocketConfigurer {

    private final CustomSystemWebSocketHandler customSystemHandler;
    private final HomePageWebSocketHandler homePageHandler;
    private final LargeRealWebSocketHandler largeRealHandler;
    private final LargeStaticWebSocketHandler largeStaticHandler;
    private final PileRealWebSocketHandler pileRealHandler;

    public WebSocketHandlerConfig(CustomSystemWebSocketHandler customSystemHandler, HomePageWebSocketHandler homePageHandler,
                                  LargeRealWebSocketHandler largeRealHandler, LargeStaticWebSocketHandler largeStaticHandler,
                                  PileRealWebSocketHandler pileRealHandler) {
        this.customSystemHandler = customSystemHandler;
        this.homePageHandler = homePageHandler;
        this.largeRealHandler = largeRealHandler;
        this.largeStaticHandler = largeStaticHandler;
        this.pileRealHandler = pileRealHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customSystemHandler, "/customSystemWebSocket/{userId}/{siteId}")
                .addHandler(homePageHandler, "/homePageWebSocket/{userId}")
                .addHandler(largeRealHandler, "/largeRealWebSocket/{userId}")
                .addHandler(largeStaticHandler, "/largeStaticWebSocket/{userId}")
                .addHandler(pileRealHandler, "/pileRealWebSocket/{userId}/{pileCode}")
                .setAllowedOrigins("*");
    }
}
