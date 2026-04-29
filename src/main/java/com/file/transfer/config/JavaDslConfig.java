package com.file.transfer.config;

import com.file.transfer.service.FileProcessingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.LoggingHandler;

import java.io.File;

@Configuration
@EnableIntegration
@Profile("java")
public class JavaDslConfig {

    private final FileProcessingService fileProcessingService;

    public JavaDslConfig(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    @Bean
    public IntegrationFlow fileTransferFlow() {
        return IntegrationFlow.from(
                        Files.inboundAdapter(new File("source")),
                        e -> e.poller(Pollers.fixedDelay(1000))
                )
                // 1. Transformer & Content Enricher:
                // Encapsulates business logic for renaming (UUID) and adding metadata (headers)
                .transform(fileProcessingService, "renameAndEnrich")

                // 2. Structured Audit Logging:
                // Using a LoggingHandler to capture metadata from message headers
                .log(LoggingHandler.Level.INFO, "audit", m ->
                        "FILE PROCESSED | " +
                                "Original Name: " + m.getHeaders().get("originalFileName") + " | " +
                                "New Name: " + m.getHeaders().get(FileHeaders.FILENAME) + " | " +
                                "Size: " + m.getHeaders().get("fileSize") + " bytes | " +
                                "Timestamp: " + m.getHeaders().get("ingestionTimestamp")
                )

                // 3. Content-Based Routing:
                // Directs the file to different target directories based on the file extension
                .<File, String>route(
                        m -> m.getName().toLowerCase().endsWith(".txt") ? "TXT" : "OTHER",
                        mapping -> mapping
                                .subFlowMapping("TXT", sf -> sf
                                        .handle(Files.outboundAdapter(new File("target_txt"))
                                                .deleteSourceFiles(true)))
                                .subFlowMapping("OTHER", sf -> sf
                                        .handle(Files.outboundAdapter(new File("target_other"))
                                                .deleteSourceFiles(true)))
                )
                .get();
    }
}