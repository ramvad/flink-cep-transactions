package com.example.config;

import org.apache.flink.configuration.Configuration;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FlinkJobConfig {
    private final Path dataPath;
    private final Configuration flinkConfig;

    public FlinkJobConfig() {
        this.flinkConfig = new Configuration();
        this.dataPath = resolveDataPath();
    }

    private Path resolveDataPath() {
        // First try environment variable
        String configuredPath = System.getenv("FLINK_DATA_PATH");
        if (configuredPath != null && !configuredPath.isEmpty()) {
            return Paths.get(configuredPath);
        }
        
        // Fallback to default path
        return Paths.get("data", "transactions.csv");
    }

    public Path getDataPath() {
        return dataPath;
    }

    public Configuration getFlinkConfig() {
        return flinkConfig;
    }
}