package dev.nexonm.distfs.storage_node.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChunkDownloadRequest {
    private String fileUUID;
    private String chunkUUID;
    private int chunkIndex;
}
