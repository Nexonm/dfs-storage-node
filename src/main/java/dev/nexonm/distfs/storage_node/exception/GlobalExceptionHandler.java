package dev.nexonm.distfs.storage_node.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Common error response structure
    private Map<String, Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message != null ? message : "An unexpected error occurred");
        response.put("status", status.value());
        return response;
    }

    // Custom Exception Handlers
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFileNotFoundException(FileNotFoundException ex) {
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    // Custom Exception Handlers
    @ExceptionHandler(ChunkNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleChunkNotFoundException(ChunkNotFoundException ex) {
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HashIsNotEqualException.class)
    public ResponseEntity<Map<String, Object>> handleHashMismatch(HashIsNotEqualException ex) {
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, Object>> handleFileStorageException(FileStorageException ex) {
        return new ResponseEntity<>(buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Global Exception Catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
