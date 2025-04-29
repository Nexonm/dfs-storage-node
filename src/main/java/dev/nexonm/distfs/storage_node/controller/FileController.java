package dev.nexonm.distfs.storage_node.controller;

import dev.nexonm.distfs.storage_node.dto.ChunkDownloadRequest;
import dev.nexonm.distfs.storage_node.dto.FileDeleteRequest;
import dev.nexonm.distfs.storage_node.dto.StoreChunkRequest;
import dev.nexonm.distfs.storage_node.exception.ChunkNotFoundException;
import dev.nexonm.distfs.storage_node.exception.FileNotFoundException;
import dev.nexonm.distfs.storage_node.service.FileDeleteService;
import dev.nexonm.distfs.storage_node.service.FileDownloadService;
import dev.nexonm.distfs.storage_node.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chunk")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileStorageService storageService;
    private final FileDownloadService downloadService;
    private final FileDeleteService deleteService;

    @PostMapping("/download")
    public ResponseEntity<Resource> downloadChunk(@RequestBody ChunkDownloadRequest request) {
        log.info("Received download request for chunk: fileId={}, chunkId={}, index={}",
                request.getFileUUID(), request.getChunkUUID(), request.getChunkIndex());

        try {
            // Get the resource from the download service
            Resource resource = downloadService.downloadChunk(request);

            // Set up the response headers
            String filename = String.format("%s_%s_chunk%d",
                    request.getFileUUID(),
                    request.getChunkUUID(),
                    request.getChunkIndex());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (ChunkNotFoundException e) {
            log.error("Chunk not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error downloading chunk", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadChunk(
            @RequestPart("file") MultipartFile file,
            @RequestPart("chunkId") String chunkUUID,
            @RequestPart("fileId") String fileUUID,
            @RequestPart("chunkIndex") String chunkIndex,
            @RequestPart("hash") String hash) {
        StoreChunkRequest request = new StoreChunkRequest(file, chunkUUID, fileUUID, Integer.parseInt(chunkIndex), hash);
        log.info("Received chunk: fileID={}, chunkID={}, index={}", request.getFileUUID(), request.getChunkUUID(),
                request.getChunkIndex());
        String filename = storageService.storeFile(request);
        return ResponseEntity.ok(String.format("File saved successfully, name=%s", filename));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestBody FileDeleteRequest request) {
        log.info("Received request to delete chunk: fileUUID={}, chunkUUID={}, chunkIndex={}",
                request.getFileUUID(), request.getChunkUUID(), request.getChunkIndex());

        try {
            boolean deleted = deleteService.deleteChunk(request);
            if (deleted) {
                return ResponseEntity.ok("Chunk deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to delete chunk");
            }
        } catch (FileNotFoundException e) {
            log.warn("Chunk not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting chunk", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting chunk: " + e.getMessage());
        }
    }
}