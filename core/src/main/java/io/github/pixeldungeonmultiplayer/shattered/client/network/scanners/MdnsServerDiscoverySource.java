package io.github.pixeldungeonmultiplayer.shattered.client.network.scanners;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.network.ServiceInfo;
import com.watabou.network.ServiceInfoHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MdnsServerDiscoverySource implements ServerDiscoverySource {
    private final Object serverListLock = new Object();
    private final Map<String, ServerInfo> serverList = new LinkedHashMap<String, ServerInfo>();
    private final ServiceInfoHandler serviceInfoHandler;
    private ServiceDiscoveryListener listener;

    public MdnsServerDiscoverySource() {
        serviceInfoHandler = ShatteredPixelDungeon.platform.createServiceInfoHandler(new Listener());
    }

    @Override
    public boolean startDiscovery(ServiceDiscoveryListener listener) {
        this.listener = listener;
        serviceInfoHandler.startDiscovery();
        return true;
    }

    @Override
    public boolean stopDiscovery() {
        serviceInfoHandler.stopDiscovery();
        synchronized (serverListLock) {
            serverList.clear();
        }
        listener = null;
        return true;
    }

    @Override
    public List<ServerInfo> getServerList() {
        synchronized (serverListLock) {
            return new ArrayList<ServerInfo>(serverList.values());
        }
    }

    private void notifyServerFound(ServerInfo info) {
        ServiceDiscoveryListener currentListener = listener;
        if (currentListener != null) {
            currentListener.onServiceFound(info);
        }
    }

    private void notifyServerLost(ServerInfo info) {
        ServiceDiscoveryListener currentListener = listener;
        if (currentListener != null) {
            currentListener.onServiceLost(info);
        }
    }

    private void notifyServerListChanged() {
        notifyServerFound(null);
    }

    private static ServerInfo fromServiceInfo(ServiceInfo info) {
        DirectServerInfo serverInfo = new DirectServerInfo(
                info.getServiceName(),
                info.getHost(),
                info.getPort(),
                propertyInt(info, "players", -1),
                propertyInt(info, "max_players", -1),
                propertyInt(info, "challenges", 0) > 0
        );
        serverInfo.serverId = info.getProperty("server_id");
        serverInfo.currentFloor = propertyInt(info, "current_floor", 0);
        serverInfo.motd = info.getProperty("motd");
        serverInfo.serverVersion = info.getProperty("server_version");
        serverInfo.serverVersionCode = propertyInt(info, "server_version_code", 0);
        serverInfo.serverProtocolVersion = propertyInt(info, "server_protocol_version", 0);
        return serverInfo;
    }

    private static int propertyInt(ServiceInfo info, String key, int defaultValue) {
        String value = info.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String serviceKey(ServiceInfo info, ServerInfo serverInfo) {
        if (serverInfo.serverId != null && !serverInfo.serverId.trim().isEmpty()) {
            return "server_id:" + serverInfo.serverId;
        }
        if (info.getHost() != null && info.getHost().getHostAddress() != null && info.getPort() != 0) {
            return info.getType() + ":" + info.getServiceName() + ":" + info.getHost().getHostAddress() + ":" + info.getPort();
        }
        return info.getType() + ":" + info.getServiceName();
    }

    private static void addOrReplaceByServerId(Map<String, ServerInfo> serverList, String key, ServerInfo serverInfo) {
        if (serverInfo.serverId != null && !serverInfo.serverId.trim().isEmpty()) {
            List<String> staleKeys = new ArrayList<String>();
            for (Map.Entry<String, ServerInfo> entry : serverList.entrySet()) {
                ServerInfo oldInfo = entry.getValue();
                if (serverInfo.serverId.equals(oldInfo.serverId)) {
                    staleKeys.add(entry.getKey());
                }
            }
            for (String staleKey : staleKeys) {
                serverList.remove(staleKey);
            }
        }
        serverList.put(key, serverInfo);
    }

    private class Listener implements com.watabou.network.ServiceInfoListener {
        @Override
        public void onServiceResolved(ServiceInfo info) {
            ServerInfo serverInfo = fromServiceInfo(info);
            synchronized (serverListLock) {
                addOrReplaceByServerId(serverList, serviceKey(info, serverInfo), serverInfo);
            }
            notifyServerListChanged();
        }

        @Override
        public void onServiceFound(ServiceInfo info) {
            notifyServerFound(fromServiceInfo(info));
        }

        @Override
        public void onServiceLost(ServiceInfo info) {
            ServerInfo serverInfo = fromServiceInfo(info);
            synchronized (serverListLock) {
                serverList.remove(serviceKey(info, serverInfo));
                if (serverInfo.serverId != null && !serverInfo.serverId.trim().isEmpty()) {
                    List<String> staleKeys = new ArrayList<String>();
                    for (Map.Entry<String, ServerInfo> entry : serverList.entrySet()) {
                        if (serverInfo.serverId.equals(entry.getValue().serverId)) {
                            staleKeys.add(entry.getKey());
                        }
                    }
                    for (String staleKey : staleKeys) {
                        serverList.remove(staleKey);
                    }
                }
            }
            notifyServerLost(serverInfo);
        }
    }
}
