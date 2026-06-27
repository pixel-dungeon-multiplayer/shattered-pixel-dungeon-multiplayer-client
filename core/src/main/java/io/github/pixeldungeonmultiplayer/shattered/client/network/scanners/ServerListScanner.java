package io.github.pixeldungeonmultiplayer.shattered.client.network.scanners;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ServerListScanner implements ServiceDiscovery, ServerDiscoverySource {
    private final Object serverListLock = new Object();
    private final List<UserServerInfo> userServerInfoList = new ArrayList<>();
    @Override
    public boolean startDiscovery(ServiceDiscoveryListener listener) {
        List<InetSocketAddress> serverList = SPDSettings.serverList();
        for (InetSocketAddress address : serverList) {
            UserServerInfo serverInfo;
            serverInfo = DirectServerParser.fromAddress(address);
            if (serverInfo == null) {
                serverInfo = new UserServerInfo(address.getHostName(), address.getAddress(), address.getPort(), 0, -1, false);
                serverInfo.online = false;
            }
            synchronized (serverListLock) {
                userServerInfoList.add(serverInfo);
            }
            if (listener != null) {
                listener.onServiceFound(serverInfo);
            }
        }
        return true;
    }

    @Override
    public boolean stopDiscovery() {
        return false;
    }

    public List<ServerInfo> getServerList() {
        synchronized (serverListLock) {
            return new ArrayList<ServerInfo>(userServerInfoList);
        }
    }
}
