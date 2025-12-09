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
import com.fasterxml.jackson.databind.ObjectMapper;

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
        ObjectMapper om = new ObjectMapper();

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
                StringBuilder eventBuf = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    // SSE separates events by blank lines; collect non-empty lines into a block
                    if (line.isBlank()) {
                        String block = eventBuf.toString().trim();
                        eventBuf.setLength(0);
                        if (!block.isEmpty()) {
                            // attempt to extract JSON payload (first '{'..last '}')
                            int s = block.indexOf('{');
                            int e = block.lastIndexOf('}');
                            if (s >= 0 && e >= s) {
                                String json = block.substring(s, e + 1);
                                received.add(json);
                            } else {
                                received.add(block);
                            }
                        }
                        continue;
                    }
                    // accumulate
                    eventBuf.append(line).append('\n');
                }
                // process any trailing buffer
                String trailing = eventBuf.toString().trim();
                if (!trailing.isEmpty()) {
                    int s = trailing.indexOf('{');
                    int e = trailing.lastIndexOf('}');
                    if (s >= 0 && e >= s) {
                        received.add(trailing.substring(s, e + 1));
                    } else {
                        received.add(trailing);
                    }
                }
            } catch (Exception e) {
                // connection closed or error
            }
        });
        reader.setDaemon(true);
        reader.start();

        // brief pause to ensure the server registered the emitter
        Thread.sleep(150);

        // publish a richer sequence of events to the job via ProgressBus
        long now = System.currentTimeMillis();
        progressBus.publish(jobId, Map.of("event", "object-start", "type", "FILE", "name", "/tmp/TestA.java", "ts", now));
        progressBus.publish(jobId, Map.of("event", "object-progress", "type", "FILE", "name", "/tmp/TestA.java", "objectWorkDone", 1, "objectTotalWork", 3, "ts", now + 10));
        progressBus.publish(jobId, Map.of("event", "progress", "jobId", jobId, "percent", 33, "filesParsed", 1, "documentsIndexed", 0, "ts", now + 20));
        progressBus.publish(jobId, Map.of("event", "object-end", "type", "FILE", "name", "/tmp/TestA.java", "elapsedMs", 123, "ts", now + 30));
        progressBus.publish(jobId, Map.of("event", "progress", "jobId", jobId, "percent", 100, "filesParsed", 1, "documentsIndexed", 0, "ts", now + 40));

        // Wait for messages to be received (be tolerant — allow a few seconds for server IO)
        long start = System.currentTimeMillis();
        boolean sawStart = false, sawProgress = false, sawEnd = false, sawFinalProgress = false;
        // Wait up to 8s for all events to arrive
        while ((System.currentTimeMillis() - start) < 8000L) {
            for (String json : received) {
                try {
                    Map<String,Object> o = om.readValue(json, Map.class);
                    String ev = (String)o.get("event");
                    if ("object-start".equals(ev)) sawStart = true;
                    if ("object-progress".equals(ev)) sawProgress = true;
                    if ("object-end".equals(ev)) sawEnd = true;
                    if ("progress".equals(ev) && Integer.valueOf(100).equals((Integer)((Number)o.get("percent")))) sawFinalProgress = true;
                } catch (Exception ex) {
                    // ignore parse errors while waiting
                }
            }
            if (sawStart && sawProgress && sawEnd && sawFinalProgress) break;
            Thread.sleep(50);
        }

        // Shut down the connection
        try { conn.disconnect(); } catch (Exception ignored) {}

        // Basic sanity: make sure we received some lines
        assertThat(received).isNotEmpty();
        if (!(sawStart && sawProgress && sawEnd && sawFinalProgress)) {
            // Dump debug info to help diagnose intermittent CI issues
            System.err.println("=== SSE RECEIVED LINES ===");
            received.forEach(l -> System.err.println(l));
        }

        // Assert we saw the expected event types
        assertThat(sawStart).withFailMessage("Expected object-start").isTrue();
        assertThat(sawProgress).withFailMessage("Expected object-progress").isTrue();
        assertThat(sawEnd).withFailMessage("Expected object-end").isTrue();
        assertThat(sawFinalProgress).withFailMessage("Expected a final progress == 100").isTrue();

        // Validate payload contents / ordering: ensure object-start happens before object-end
        int idxStart = -1, idxProgress = -1, idxEnd = -1;
        for (int i = 0; i < received.size(); i++) {
            String payload = ((String[])received.toArray(new String[0]))[i];
            try {
                Map<String,Object> o = om.readValue(payload, Map.class);
                String ev = (String)o.get("event");
                if ("object-start".equals(ev)) idxStart = i;
                if ("object-progress".equals(ev)) idxProgress = i;
                if ("object-end".equals(ev)) idxEnd = i;
            } catch (Exception ex) {
                // ignore
            }
        }
        assertThat(idxStart).isGreaterThanOrEqualTo(0);
        assertThat(idxEnd).isGreaterThanOrEqualTo(0);
        assertThat(idxProgress).isGreaterThanOrEqualTo(0);
        assertThat(idxStart).isLessThan(idxEnd);
    }
}
