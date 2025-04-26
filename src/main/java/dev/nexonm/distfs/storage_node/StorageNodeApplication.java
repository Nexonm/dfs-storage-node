package dev.nexonm.distfs.storage_node;

import dev.nexonm.distfs.storage_node.config.FileStorageProperties;
import dev.nexonm.distfs.storage_node.service.NodeRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
@Slf4j
public class StorageNodeApplication {

	@Autowired
	private NodeRegisterService nodeRegisterService;

	public static void main(String[] args) {
		SpringApplication.run(StorageNodeApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void sendRegisterRequest() {
		log.info("Application started. Sending server information...");
		nodeRegisterService.sendRegisterRequest();
	}

}
