package dev.nexonm.distfs.storage_node;

import dev.nexonm.distfs.storage_node.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class StorageNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorageNodeApplication.class, args);
	}

}
