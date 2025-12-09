package com.codetalker.firestick.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogsController {

    @GetMapping(value = "/latest", produces = MediaType.TEXT_PLAIN_VALUE)
    public String latest() throws IOException {
        File logsDir = new File("logs");
        if (!logsDir.exists() || !logsDir.isDirectory()) return "No logs directory found";

        // find newest file under logs (including nested directories)
        File newest = Arrays.stream(logsDir.listFiles(file -> true))
                .flatMap(f -> f.isDirectory() ? Arrays.stream(f.listFiles()) : Arrays.stream(new File[]{f}))
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);

        if (newest == null) return "No log files found";

        return Files.readString(newest.toPath());
    }
}
