# Distributed File Storage Node

A Spring Boot service responsible for storing file chunks and serving them on demand. Integrates with the metadata server to ensure reliable distributed storage with chunk replication and integrity checks.

## Key Features
- **Chunk Storage**  
  Stores file chunks with SHA-256 hash verification
- **Replication Support**  
  Works with metadata server to maintain chunk replicas across nodes
- **Health Reporting**  
  Metadata checks health beat every 15 seconds
- **Chunk Integrity**  
  Hash verification on storage and retrieval


## Tech Stack
- **Core**: Java 21, Spring Boot 3.4.4
- **Networking**: WebClient, Reactive Streams
- **Containerization**: Docker 24.0

## Quick Start

### Prerequisites
- Docker Engine 24.0+
- Metadata server running (from [dfs-metadata](https://github.com/Nexonm/dfs-metadata))

### Basic Node Setup
1. Build image:
```bash
docker build -t dfs-storage-node .
```

2. Configure the start command:

| Variable    | Required | Param       | Example | Description          |
|-------------|----------|-------------|---------|----------------------|
| `META_HOST` | Yes | <meta_ip>   | 192.168.1.10 | Metadata server IP   |
| `META_PORT` | Yes | <meta_port> | 8080 | Metadata server port |
| `NODE_HOST` | Yes | <node_ip>   | 192.168.1.20 | Current node IP      |
| `NODE_PORT` | Yes | <this_port> | 8100 | Exposed node port    |
```bash
docker run -d \
  --name storage-node-0 \
  -p <this_port>:8080 \
  -e META_HOST=<meta_ip> \
  -e META_PORT=<meta_port> \
  -e NODE_HOST=<node_ip> \
  -e NODE_PORT=<this_port> \
  dfs-storage-node
```

### Multi-Node Cluster Example
```powershell
# Node 1
docker run -d --name node-0 -p 8100:8080 -e META_HOST=192.168.1.10 -e META_PORT=8080 -e NODE_HOST=192.168.1.20 -e NODE_PORT=8100 dfs-storage-node

# Node 2 
docker run -d --name node-1 -p 8101:8080 -e META_HOST=192.168.1.10 -e META_PORT=8080 -e NODE_HOST=192.168.1.20 -e NODE_PORT=8101 dfs-storage-node
```

## API Documentation
### Chunk Retrieval Endpoint

**Endpoint:**
```http
POST /api/chunk/download
Content-Type: application/json
```

**Request Body:**
```json
{
  "fileUUID": "string (UUID)",
  "chunkUUID": "string (UUID)", 
  "chunkIndex": "integer"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/chunk \
  -H "Content-Type: application/json" \
  -d '{
    "fileUUID": "21b57063-babf-4e87-8b87-8e988ff35a07",
    "chunkUUID": "4a9e0bf4-36ab-4165-828e-d3d128d6396b",
    "chunkIndex": 1
  }'
```

**Response:**
- Status: 200 OK
- Content-Type: application/octet-stream
- Body: Raw chunk bytes

**Error Codes:**

| Code | Status | Description |
|------|--------|-------------|
| 400 | Bad Request | Invalid UUID format |
| 404 | Not Found | Chunk not found |
| 500 | Internal Error | Storage corruption detected |




