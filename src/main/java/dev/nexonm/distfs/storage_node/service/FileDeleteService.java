package dev.nexonm.distfs.storage_node.service;

import dev.nexonm.distfs.storage_node.config.FileStorageProperties;
import dev.nexonm.distfs.storage_node.dto.FileDeleteRequest;
import dev.nexonm.distfs.storage_node.exception.FileNotFoundException;
import dev.nexonm.distfs.storage_node.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileDeleteService {
    private final Path fileStorageLocation;

    public FileDeleteService(FileStorageProperties properties) {
        this.fileStorageLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
    }

    /**
     * Deletes a specific file chunk based on the provided request
     *
     * @param request Contains details about which chunk to delete
     * @return true if deletion was successful
     */
    public boolean deleteChunk(FileDeleteRequest request) {
        String fileName = generateFileName(request.getFileUUID(), request.getChunkUUID(), request.getChunkIndex());
        Path filePath = this.fileStorageLocation.resolve(fileName);

        log.info("Attempting to delete chunk: {}", fileName);

        if (!Files.exists(filePath)) {
            log.error("File {} does not exist", fileName);
            throw new FileNotFoundException("The chunk " + fileName + " does not exist");
        }

        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Successfully deleted chunk: {}", fileName);
                return true;
            } else {
                log.warn("Unable to delete chunk: {}", fileName);
                return false;
            }
        } catch (IOException ex) {
            log.error("Error occurred while deleting chunk: {}", fileName, ex);
            throw new FileStorageException("Could not delete file " + fileName, ex);
        }
    }

    /**
     * Generates the filename for a chunk based on the same pattern used in FileStorageService
     */
    private String generateFileName(String fileUUID, String chunkUUID, int chunkIndex) {
        return String.format("%s_%s_chunk%d", fileUUID, chunkUUID, chunkIndex);
    }
}
