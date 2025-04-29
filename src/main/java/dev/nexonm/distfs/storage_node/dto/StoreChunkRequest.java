package dev.nexonm.distfs.storage_node.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreChunkRequest {

    MultipartFile chunkData;
    String chunkUUID;
    String fileUUID;
    int chunkIndex;
    String hash;
}
