# dfs-storage-node
Distributed File System Metadata Storage Node repository. It stores chunk of bytes of file and returns it.


## Run commands
For run you should specify 4 environmental variables:
- SERVER_HOST - ip of the metadata server;
- SERVER_PORT - port of the metadata server;
- NODE_HOST - ip of the current node;
- NODE_PORT - port of the current node.

Build example:
```bash
docker build -t dfs-storage-node .
```
Linux run example:
```bash
docker run -d \
--name <container_name> \
-p <local_port>:8080 \
-e SERVER_HOST=<server_ip> \
-e SERVER_PORT=<server_port> \
-e NODE_HOST=<local_ip> \
-e NODE_PORT=<local_port> \
dfs-storage-node
```

Fast windows run (add both ip values):
```powershell
docker run -d --name dfs-node0 -p 8100:8080 -e SERVER_HOST= -e SERVER_PORT=8080 -e NODE_HOST= -e NODE_PORT=8100 dfs-storage-node
```



