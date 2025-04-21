package dev.nexonm.distfs.storage_node.service;

import dev.nexonm.distfs.storage_node.config.FileStorageProperties;
import dev.nexonm.distfs.storage_node.dto.ChunkDownloadRequest;
import dev.nexonm.distfs.storage_node.exception.ChunkNotFoundException;
import dev.nexonm.distfs.storage_node.exception.FileStorageException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileDownloadService {

    private final Path fileStorageLocation;

    public FileDownloadService(FileStorageProperties properties) {
        this.fileStorageLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
    }

    /**
     * Downloads a specific chunk as a byte array
     *
     * @param request contains file and chunk UUID and chunk sequence id
     * @return Resource containing the chunk data
     */
    public Resource downloadChunk(ChunkDownloadRequest request) {
        try {
            String fileName = generateFileName(request.getFileUUID(), request.getChunkUUID(), request.getChunkIndex());
            Path chunkPath = this.fileStorageLocation.resolve(fileName).normalize();

            // Check if the file exists
            if (!Files.exists(chunkPath)) {
                throw new ChunkNotFoundException("Chunk not found: " + fileName);
            }

            // Verify the file is within the target directory (security check)
            if (!chunkPath.toAbsolutePath().startsWith(this.fileStorageLocation.toAbsolutePath())) {
                throw new FileStorageException("Chunk path is outside the allowed directory");
            }

            // Read the file bytes
            byte[] data = Files.readAllBytes(chunkPath);

            log.info("Retrieved chunk: {}", fileName);
            return new ByteArrayResource(data);

        } catch (IOException ex) {
            throw new FileStorageException("Could not read chunk file", ex);
        }
    }

    /**
     * Generates a consistent filename for a chunk based on its identifiers
     */
    private String generateFileName(String fileUUID, String chunkUUID, int chunkIndex) {
        return String.format("%s_%s_chunk%d", fileUUID, chunkUUID, chunkIndex);
    }
}
