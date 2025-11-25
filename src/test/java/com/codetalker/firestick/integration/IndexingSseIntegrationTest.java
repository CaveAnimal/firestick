package com.codetalker.firestick.integration;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.ProgressBus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class IndexingSseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProgressBus progressBus;

    @Autowired
    private IndexingJobRepository jobRepository;

    @Test
    void sse_stream_receives_published_events() throws Exception {
        // Create a job placeholder so the stream controller can locate it
        IndexingJob j = new IndexingJob();
        j.setRootPath("/tmp/test");
        j.setStatus(IndexingJob.Status.RUNNING);
        j.setStartedAt(Instant.now());
        IndexingJob saved = jobRepository.save(j);
        final Long jobId = saved.getId();

        Queue<String> received = new ConcurrentLinkedQueue<>();

        // Open a raw HTTP connection and read the streaming response lines — reliable for SSE in tests
        java.net.URL url = new java.net.URL("http://localhost:" + port + "/api/indexing/stream?jobId=" + jobId);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "text/event-stream");
        conn.setReadTimeout(0);
        conn.connect();

        Thread reader = new Thread(() -> {
            try (var in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    if (!line.isBlank()) received.add(line);
                }
            } catch (Exception e) {
                // connection closed or error
            }
        });
        reader.setDaemon(true);
        reader.start();

        // brief pause to ensure the server registered the emitter
        Thread.sleep(150);

        // publish a couple of events to the job via ProgressBus
        progressBus.publish(jobId, Map.of("event", "object-start", "type", "FILE", "name", "/tmp/TestA.java"));
        progressBus.publish(jobId, Map.of("event", "object-end", "type", "FILE", "name", "/tmp/TestA.java", "elapsedMs", 42));

        // Wait for messages to be received
        long start = System.currentTimeMillis();
        while (received.isEmpty() && (System.currentTimeMillis() - start) < 3000L) {
            Thread.sleep(50);
        }

        // Shut down the connection
        try { conn.disconnect(); } catch (Exception ignored) {}

        assertThat(received).isNotEmpty();
        boolean hasStart = received.stream().anyMatch(s -> s.contains("object-start"));
        boolean hasEnd = received.stream().anyMatch(s -> s.contains("object-end"));
        assertThat(hasStart).isTrue();
        assertThat(hasEnd).isTrue();
    }
}
