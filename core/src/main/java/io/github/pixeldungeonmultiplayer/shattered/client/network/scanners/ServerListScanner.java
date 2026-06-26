package io.github.pixeldungeonmultiplayer.shattered.client.network.scanners;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerListScanner implements ServiceDiscovery {
    List<UserServerInfo> userServerInfoList = new ArrayList<>();
    @Override
    public boolean startDiscovery(ServiceDiscoveryListener listener) {
        Socket socket = new Socket();
        List<InetSocketAddress> serverList = SPDSettings.serverList();
        for (InetSocketAddress address : serverList) {
            UserServerInfo serverInfo;
            serverInfo = DirectServerParser.fromAddress(address);
            if (serverInfo == null) {
                serverInfo = new UserServerInfo(address.getHostName(), address.getAddress(), address.getPort(), 0, -1, false);
                serverInfo.online = false;
            }
            userServerInfoList.add(serverInfo);
            listener.onServiceFound(serverInfo);
        }
        return true;
    }

    @Override
    public boolean stopDiscovery() {
        return false;
    }

    public Collection<? extends ServerInfo> getServerList() {
        return userServerInfoList;
    }
}
