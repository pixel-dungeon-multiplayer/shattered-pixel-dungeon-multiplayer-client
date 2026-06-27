package io.github.pixeldungeonmultiplayer.shattered.client.network.scanners;

import java.util.List;

public interface ServerDiscoverySource extends ServiceDiscovery {
    List<ServerInfo> getServerList();
}
