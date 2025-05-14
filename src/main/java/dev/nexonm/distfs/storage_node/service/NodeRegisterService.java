package dev.nexonm.distfs.storage_node.service;

import dev.nexonm.distfs.storage_node.config.MetadataConfig;
import dev.nexonm.distfs.storage_node.dto.ServerInfoRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@AllArgsConstructor
@Slf4j
public class NodeRegisterService {

    private MetadataConfig metadataConfig;
    private RestTemplate restTemplate;

    public void sendRegisterRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ServerInfoRequest data = new ServerInfoRequest(
                metadataConfig.getNodeHost(),
                Integer.parseInt(metadataConfig.getNodePort())
        );

        HttpEntity<ServerInfoRequest> requestEntity = new HttpEntity<>(data, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getUrl(), requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()){
            log.info("Storage node was successfully registered.");
        }else{
            log.error("Storage node was not registered. Status code: {}.", response.getStatusCode());
        }
    }

    private String getUrl() {
        return String.format("https://%s:%s/api/node/register", metadataConfig.getMetaHost(),
                metadataConfig.getMetaPort());
    }


}
