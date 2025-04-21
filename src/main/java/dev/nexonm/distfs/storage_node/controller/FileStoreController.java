package dev.nexonm.distfs.storage_node.controller;

import dev.nexonm.distfs.storage_node.dto.StoreChunkRequest;
import dev.nexonm.distfs.storage_node.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chunk")
@RequiredArgsConstructor
@Slf4j
public class FileStoreController {
    private final FileStorageService service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadChunk(
            @RequestPart("file") MultipartFile file,
            @RequestPart("chunkId") String chunkUUID,
            @RequestPart("fileId") String fileUUID,
            @RequestPart("chunkIndex") String chunkIndex) {
        StoreChunkRequest request = new StoreChunkRequest(file, chunkUUID, fileUUID, Integer.parseInt(chunkIndex));
        log.info("Received chunk: ID={}, fileId={}, index={}", request.getChunkUUID(), request.getFileUUID(),
                request.getChunkIndex());
        service.storeFile(request);
        return ResponseEntity.ok("File saved successfully.");
    }
}