package dev.nexonm.distfs.storage_node.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfoRequest {
    String host;
    Integer port;
}
