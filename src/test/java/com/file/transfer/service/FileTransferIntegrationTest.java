package com.file.transfer.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("java") // Test the Java DSL configuration specifically
class FileTransferIntegrationTest {

    @Test
    void shouldTransferFileFromSourceToTarget() throws IOException {

        // Arrange: Create a test file in the 'source' directory monitored by the adapter
        Path sourcePath = Path.of("source/test-integration.txt");
        Files.writeString(sourcePath, "Integration test content: " + System.currentTimeMillis());

        try {
            // Act & Assert: Wait for the poller to pick up and process the file.
            // Using Awaitility because the integration flow operates asynchronously.
            await()
                    .atMost(5, SECONDS)
                    .pollInterval(1, SECONDS)
                    .untilAsserted(() -> {
                        File targetDir = new File("target_txt");
                        File[] files = targetDir.listFiles((dir, name) -> name.endsWith("test-integration.txt"));

                        assertTrue(files != null && files.length > 0,
                                "The file should have been processed and moved to target_txt");
                    });
        } finally {
            // Clean up: Ensure test artifacts are removed regardless of test outcome
            cleanUpTargetFile("target_txt", "test-integration.txt");
        }
    }

    private void cleanUpTargetFile(String directory, String fileNameSuffix) {
        File targetDir = new File(directory);
        File[] found = targetDir.listFiles((dir, name) -> name.endsWith(fileNameSuffix));
        if (found != null) {
            for (File f : found) {
                f.delete();
            }
        }
    }
}