package com.file.transfer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.Message;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {

    private final FileProcessingService service = new FileProcessingService();

    @Test
    void shouldEnrichMessageWithMetadata() {
        // Arrange: Setup a mock file
        File tempFile = new File("test.txt");

        // Act: Process the file through the service logic
        Message<File> result = service.renameAndEnrich(tempFile);

        // Assert: Verify metadata enrichment and unique naming
        assertNotNull(result.getHeaders().get("originalFileName"));
        assertNotNull(result.getHeaders().get("fileSize"));
        assertNotNull(result.getHeaders().get("ingestionTimestamp"));

        String newName = (String) result.getHeaders().get(FileHeaders.FILENAME);
        assertTrue(newName.contains("test.txt"));

        // Validate UUID prefix format using regex
        assertTrue(newName.matches("^[0-9a-fA-F-]{36}_test.txt$"));
    }
}