package dev.nexonm.distfs.storage_node.service;

import dev.nexonm.distfs.storage_node.config.FileStorageProperties;
import dev.nexonm.distfs.storage_node.dto.StoreChunkRequest;
import dev.nexonm.distfs.storage_node.exception.FileStorageException;
import dev.nexonm.distfs.storage_node.exception.HashIsNotEqualException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {
    private final Path fileStorageLocation;
    private final HashGenerationService hashGenerationService;

    public FileStorageService(FileStorageProperties properties, HashGenerationService hashGenerationService) {
        this.fileStorageLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
        this.hashGenerationService = hashGenerationService;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
                    ex);
        }
    }

    public String storeFile(StoreChunkRequest request) {
        validateFileHash(request.getChunkData(), request.getHash());

        String fileName = generateBaseFileName(request.getFileUUID(), request.getChunkUUID(), request.getChunkIndex());

        try {
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(request.getChunkData().getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File saved successfully with name: {}", fileName);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    private String generateBaseFileName(String fileUUID, String chunkUUID, int chunkIndex) {

        return String.format("%s_%s_chunk%d", fileUUID, chunkUUID, chunkIndex);
    }

    /**
     * Checks if file and hash are correctly provided and no corruption occurred.
     *
     * @param file file data
     * @param providedHash file hash from frontend
     */
    private void validateFileHash(MultipartFile file, String providedHash) {
        if (file == null) {
            log.error("Provided chunk is null.");
            throw new IllegalArgumentException("File is null.");
        }
        if (providedHash == null || providedHash.isBlank()) {
            log.error("Provided hash string is null or empty.");
            throw new IllegalArgumentException("Hash is null or empty.");
        }
        String calculatedHash = hashGenerationService.generateFileHash(file);

        if (!calculatedHash.equalsIgnoreCase(providedHash)) {
            log.error("Hash Verification: FALSE. Provided hash \"{}\" doesn't equal to local test \"{}\"", providedHash,
                    calculatedHash);
            throw new HashIsNotEqualException(
                    String.format("Provided hash \"%s\" doesn't equal to local test \"%s\"",
                            providedHash, calculatedHash));
        }
        log.info("Hash Verification: TRUE. Provided hash equals to local test \"{}\"", calculatedHash);
    }

}

