package com.codetalker.firestick.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SseConfig {

    @Bean(name = "sseHeartbeatExecutor")
    public ScheduledExecutorService sseHeartbeatExecutor() {
        ThreadFactory tf = r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("sse-heartbeat");
            return t;
        };
        // Single thread is sufficient for lightweight heartbeats
        return Executors.newSingleThreadScheduledExecutor(tf);
    }
}
