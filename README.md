# File Transfer Service

A Spring Boot-based integration service designed to monitor a directory, process incoming files with metadata enrichment, and route them to specific target directories based on file types.

## Features

*   **Dual Configuration Support:** Implementation of both modern Spring Integration Java DSL and сlassic XML Configuration.
*   **Content Enrichment:** Every processed file is renamed with a unique UUID prefix and enriched with audit metadata (original filename, file size, and ingestion timestamp) stored in message headers.
*   **Structured Routing:** Automatic file distribution:
    *   `.txt` files are routed to the `target_txt` directory.
    *   All other file types are routed to the `target_other` directory.
*   **Audit Logging:** Integrated logging of processed file details via `LoggingHandler`.

## Technical Stack

*   **Java 21**
*   **Spring Boot 3.5.14**
*   **Spring Integration** (File Support)
*   **JUnit 5 & Mockito** (Testing)
*   **Awaitility** (Asynchronous testing)

## Getting Started

### Prerequisites

Ensure you have the following directories in the project root:

*   `source` (Input directory)
*   `target_txt` (Output for text files)
*   `target_other` (Output for other files)

### Running the Application

You can switch between configurations using Spring Profiles:

#### Option 1: Java DSL Configuration (Modern)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=java
```

#### Option 2: XML Configuration (Legacy)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=xml
```

## Testing

The project includes a comprehensive test suite to ensure reliability:

*   **Unit Tests (`FileProcessingServiceTest`):** Verifies the core business logic, ensuring files are correctly renamed and headers are populated with the required metadata.
*   **Integration Tests (`FileTransferIntegrationTest`):** Validates the end-to-end flow from the inbound adapter through the router to the final file system destination using `ActiveProfiles("java")` and Awaitility for asynchronous verification.

## Project Structure

*   `com.file.transfer.config`: Contains `JavaDslConfig` for the fluent API setup.
*   `com.file.transfer.service`: Contains `FileProcessingService` — the core logic.
*   `src/main/resources`: Contains `integration-config.xml` for the XML-based setup.
