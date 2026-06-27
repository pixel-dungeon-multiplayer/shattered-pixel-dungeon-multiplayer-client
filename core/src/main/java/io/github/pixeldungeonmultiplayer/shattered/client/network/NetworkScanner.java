package io.github.pixeldungeonmultiplayer.shattered.client.network;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.scanners.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NetworkScanner {
    protected static NetworkScannerListener scannerListener;
    private static final List<ServerDiscoverySource> discoverySources = new ArrayList<ServerDiscoverySource>();
    private static final Object scannerLock = new Object();

    public static boolean start(@NotNull NetworkScannerListener scannerListener) {
        initListener();
        final List<ServerDiscoverySource> sources = new ArrayList<ServerDiscoverySource>();
        sources.add(new MdnsServerDiscoverySource());
        if (ShatteredPixelDungeon.onlineMode()) {
            sources.add(new RelaySD(RelaySD.Protocol.V2, RelaySD.getRelayAddress(), RelaySD.getNewRelayPort()));
            //sources.add(new RelaySD(RelaySD.Protocol.V1, RelaySD.getRelayAddress(), com.shatteredpixel.shatteredpixeldungeon.SPDSettings.legacyRelayServerPort));
        }
        sources.add(new ServerListScanner());
        synchronized (scannerLock) {
            NetworkScanner.scannerListener = scannerListener;
            discoverySources.clear();
            discoverySources.addAll(sources);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ServerDiscoverySource source : sources) {
                    source.startDiscovery(listener);
                }
            }
        }, "Network scanner start").start();
        return true;
    }

    public static boolean stop() {
        final List<ServerDiscoverySource> sources;
        synchronized (scannerLock) {
            sources = new ArrayList<ServerDiscoverySource>(discoverySources);
            discoverySources.clear();
            scannerListener = null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ServerDiscoverySource source : sources) {
                    source.stopDiscovery();
                }
            }
        }, "Network scanner stop").start();
        return true;
    }

    public static List<ServerInfo> getServerList() {
        List<ServerInfo> result = new ArrayList<ServerInfo>();
        List<ServerDiscoverySource> sources;
        synchronized (scannerLock) {
            sources = new ArrayList<ServerDiscoverySource>(discoverySources);
        }
        for (ServerDiscoverySource source : sources) {
            result.addAll(source.getServerList());
        }
        return result;
    }

    private static void notifyServerFound(ServerInfo info) {
        NetworkScannerListener listener;
        synchronized (scannerLock) {
            listener = scannerListener;
        }
        if (listener != null) {
            listener.OnServerFound(info);
        }
    }

    private static void notifyServerLost(ServerInfo info) {
        NetworkScannerListener listener;
        synchronized (scannerLock) {
            listener = scannerListener;
        }
        if (listener != null) {
            listener.OnServerLost(info);
        }
    }

    private static void notifyServerListChanged() {
        NetworkScannerListener listener;
        synchronized (scannerLock) {
            listener = scannerListener;
        }
        if (listener != null) {
            listener.OnServerFound(null);
        }
    }

    protected static ServiceDiscovery.ServiceDiscoveryListener listener = null;

    protected static void initListener() {
        listener = new ServiceDiscovery.ServiceDiscoveryListener() {
            public void onServiceFound(ServerInfo info) {
                notifyServerFound(info);
            }
            public void onServiceLost(ServerInfo info) {
                notifyServerLost(info);
            }
        }
        ;
    }

    public interface NetworkScannerListener {
        public void OnServerFound(ServerInfo info);
        public void OnServerLost(ServerInfo info);
    }

}
