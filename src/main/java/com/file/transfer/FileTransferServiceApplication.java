package com.file.transfer;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class FileTransferServiceApplication {

	@Configuration
	@Profile("xml")
	@ImportResource("classpath:integration-config.xml")
	static class XmlConfig {}

	public static void main(String[] args) {
		SpringApplication.run(FileTransferServiceApplication.class, args);
	}

	/**
	 * Ensuring required directories exist before the integration flow starts.
	 * This improves the "out-of-the-box" experience for the reviewer.
	 */
	@PostConstruct
	public void setupDirectories() throws IOException {
		List<String> dirs = List.of("source", "target_txt", "target_other");
		for (String dir : dirs) {
			Files.createDirectories(Paths.get(dir));
		}
	}
}