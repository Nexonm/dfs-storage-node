package dev.nexonm.distfs.storage_node.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Getter
@Slf4j
public class MetadataConfig {

    @Value("${server.host:localhost}")
    private String serverHost;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${node.host:localhost}")
    private String nodeHost;

    @Value("${node.port:8090}")
    private String nodePort;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void logConfig() {
        log.info("Metadata server configuration: host={}, port={}", serverHost, serverPort);
        log.info("Will notify to: http://{}:{}/api/node/register", serverHost, serverPort);
        log.info("Node server configuration: host={}, port={}", nodeHost, nodePort);
    }

}
