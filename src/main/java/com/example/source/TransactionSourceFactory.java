package com.example.source;

import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineFormat;
import org.apache.flink.core.fs.Path;
import java.time.Duration;
import java.nio.file.Paths;

public class TransactionSourceFactory {
    public static FileSource<String> createSource(java.nio.file.Path dataPath) {
        // Convert java.nio.file.Path to org.apache.flink.core.fs.Path
        Path flinkPath = new Path(dataPath.toAbsolutePath().toString());
        
        return FileSource.forRecordStreamFormat(
                new TextLineFormat(),
                flinkPath)
            .monitorContinuously(Duration.ofMillis(100))
            .build();
    }
}