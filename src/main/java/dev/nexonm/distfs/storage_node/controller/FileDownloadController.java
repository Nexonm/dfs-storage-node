package dev.nexonm.distfs.storage_node.controller;

import dev.nexonm.distfs.storage_node.dto.ChunkDownloadRequest;
import dev.nexonm.distfs.storage_node.exception.ChunkNotFoundException;
import dev.nexonm.distfs.storage_node.service.FileDownloadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chunk")
@RequiredArgsConstructor
@Slf4j
public class FileDownloadController {

    private final FileDownloadService downloadService;

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
}
