package com.codetalker.firestick.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sse")
public class SseProperties {
    /** Heartbeat interval in milliseconds for SSE keep-alive comments. */
    private long heartbeatMs = 15000; // 15s default

    public long getHeartbeatMs() {
        return heartbeatMs;
    }

    public void setHeartbeatMs(long heartbeatMs) {
        this.heartbeatMs = heartbeatMs;
    }
}
