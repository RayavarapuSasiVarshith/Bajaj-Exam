package com.example.bfh.service.storage;

import com.example.bfh.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ResultStorage {
    private static final Logger log = LoggerFactory.getLogger(ResultStorage.class);
    private final AppProperties props;

    public ResultStorage(AppProperties props) {
        this.props = props;
    }

    public Path saveFinalQuery(String finalQuery) {
        Path path = Path.of(props.getStorePath());
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, finalQuery, StandardCharsets.UTF_8);
            log.info("Saved final query to {}", path.toAbsolutePath());
            return path.toAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to save final query: {}", e.getMessage());
            return path.toAbsolutePath();
        }
    }
}
