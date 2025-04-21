package dev.nexonm.distfs.storage_node.service;

import dev.nexonm.distfs.storage_node.config.FileStorageProperties;
import dev.nexonm.distfs.storage_node.dto.StoreChunkRequest;
import dev.nexonm.distfs.storage_node.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {
    private final Path fileStorageLocation;

    public FileStorageService(FileStorageProperties properties) {
        this.fileStorageLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(StoreChunkRequest request) {
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

}

