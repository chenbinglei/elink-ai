package com.sunmax.webapp.task;

import com.sunmax.webapp.config.websocket.AppRealWebSocket;
import com.sunmax.webapp.config.websocket.AppStartWebSocket;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;


@Configuration
public class PushRealTimeDataTask {
    /**
     * 每隔10秒执行一次
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void sendPileData() throws IOException {
        AppStartWebSocket.externalSendMessage();
        AppRealWebSocket.externalSendMessage();
    }
}
