package dev.nexonm.distfs.storage_node.exception;

public class ChunkNotFoundException extends RuntimeException {

  public ChunkNotFoundException(String message) {
    super(message);
  }

  public ChunkNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
