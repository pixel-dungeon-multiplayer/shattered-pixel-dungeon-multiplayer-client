package io.github.pixeldungeonmultiplayer.shattered.client.network;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.network.ServiceInfo;
import com.watabou.network.ServiceInfoHandler;
import io.github.pixeldungeonmultiplayer.shattered.client.network.scanners.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NetworkScanner {
    protected static NetworkScannerListener scannerListener;
    protected static RelaySD relayServer = null;
    protected static RelaySD legacyRelayServer = null;
    protected static ServerListScanner serverListScanner;
    protected static ServiceInfoListener serviceInfoListener = new ServiceInfoListener();
    protected static ServiceInfoHandler serviceInfoHandler = ShatteredPixelDungeon.platform.createServiceInfoHandler(serviceInfoListener);

    public static boolean start(@NotNull NetworkScannerListener scannerListener) {
        boolean res = true;
        initListener();
        NetworkScanner.scannerListener = scannerListener;
        serverListScanner = new ServerListScanner();
        serverListScanner.startDiscovery(listener);
        serviceInfoHandler.startDiscovery();
        if (ShatteredPixelDungeon.onlineMode()) {
            relayServer = new RelaySD(RelaySD.Protocol.V2, RelaySD.getRelayAddress(), RelaySD.getNewRelayPort());
            res &= relayServer.startDiscovery(listener);
            legacyRelayServer = new RelaySD(RelaySD.Protocol.V1, RelaySD.getRelayAddress(), com.shatteredpixel.shatteredpixeldungeon.SPDSettings.legacyRelayServerPort);
            res &= legacyRelayServer.startDiscovery(listener);
        }
        return res;
    }

    public static boolean stop() {
        boolean res = true;
        if (relayServer != null) {
            res &= relayServer.stopDiscovery();
            relayServer = null;
        }
        if (legacyRelayServer != null) {
            res &= legacyRelayServer.stopDiscovery();
            legacyRelayServer = null;
        }
        serviceInfoHandler.stopDiscovery();
        serviceInfoListener.serverList.clear();
        scannerListener = null;
        return res;
    }

    public static List<ServerInfo> getServerList() {
        List<ServerInfo> result = new ArrayList<ServerInfo>();
        result.addAll(serviceInfoListener.getServerList());
        if (relayServer != null) {
            result.addAll(relayServer.getServerList());
        }
        if (legacyRelayServer != null) {
            result.addAll(legacyRelayServer.getServerList());
        }
        result.addAll(serverListScanner.getServerList());
        return result;
    }

    protected static ServiceDiscovery.ServiceDiscoveryListener listener = null;

    protected static void initListener() {
        listener = new ServiceDiscovery.ServiceDiscoveryListener() {
            public void onServiceFound(ServerInfo info) {
                if (scannerListener != null)
                    scannerListener.OnServerFound(info);
            }
            public void onServiceLost(ServerInfo info) {
                scannerListener.OnServerFound(info);
            }
        }
        ;
    }

    public interface NetworkScannerListener {
        public void OnServerFound(ServerInfo info);
        public void OnServerLost(ServerInfo info);
    }

    private static class ServiceInfoListener implements com.watabou.network.ServiceInfoListener {
        protected ArrayList<ServerInfo> serverList = new ArrayList<>();
        @Override
        public void onServiceResolved(ServiceInfo info) {
            serverList.add(fromServiceInfo(info));
        }

        @Override
        public void onServiceFound(ServiceInfo info) {
            scannerListener.OnServerFound(fromServiceInfo(info));
        }

        @Override
        public void onServiceLost(ServiceInfo info) {
            scannerListener.OnServerLost(fromServiceInfo(info));
        }
        private ServerInfo fromServiceInfo(ServiceInfo info){
            DirectServerInfo serverInfo = new DirectServerInfo(
                    info.getServiceName(),
                    info.getHost(),
                    info.getPort(),
                    propertyInt(info, "players", -1),
                    propertyInt(info, "max_players", -1),
                    propertyInt(info, "challenges", 0) > 0
            );
            serverInfo.currentFloor = propertyInt(info, "current_floor", 0);
            serverInfo.motd = info.getProperty("motd");
            serverInfo.serverVersion = info.getProperty("server_version");
            serverInfo.serverVersionCode = propertyInt(info, "server_version_code", 0);
            serverInfo.serverProtocolVersion = propertyInt(info, "server_protocol_version", 0);
            return serverInfo;
        }

        private int propertyInt(ServiceInfo info, String key, int defaultValue) {
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

        public ArrayList<ServerInfo> getServerList() {
            return serverList;
        }
    }
}
