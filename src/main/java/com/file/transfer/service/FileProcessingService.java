package com.file.transfer.service;

import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsible for business logic:
 * renaming files with UUID and enriching message headers with audit metadata.
 */
@Service
public class FileProcessingService {

    public Message<File> renameAndEnrich(File file) {
        // Generating a unique name to prevent file collisions in target directories
        String newName = UUID.randomUUID() + "_" + file.getName();

        return MessageBuilder.withPayload(file)
                // This header tells the outbound adapter to use this new name when writing to disk
                .setHeader(FileHeaders.FILENAME, newName)
                .setHeader("originalFileName", file.getName())
                .setHeader("fileSize", file.length())
                .setHeader("ingestionTimestamp", LocalDateTime.now())
                .build();
    }
}