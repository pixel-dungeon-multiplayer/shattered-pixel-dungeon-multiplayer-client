package io.github.pixeldungeonmultiplayer.shattered.client.network.scanners;


import io.github.pixeldungeonmultiplayer.shattered.client.network.ServerAddress;
import io.github.pixeldungeonmultiplayer.shattered.client.network.Client;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public abstract class ServerInfo {
    public String name = "no-name";
    public String serverId;
    public int players = 0;
    public int maxPlayers = 0;
    public boolean haveChallenges = false;
    public int currentFloor = 0;
    public String motd;
    public String serverVersion;
    public int serverVersionCode = 0;
    public int serverProtocolVersion = 0;
    public abstract ServerAddress getAddress();
    public boolean connect(){
        ServerAddress address = getAddress();
        if (address == null) {
            return false;
        }
        return Client.connect(address.host, address.port);
    }
    public int icon(){
        return ItemSpriteSheet.CHEST;
    }

    public String discoveryKey() {
        if (serverId != null && !serverId.trim().isEmpty()) {
            return "server_id:" + serverId;
        }
        ServerAddress address = getAddress();
        if (address == null || address.host == null || address.host.trim().isEmpty() || address.port == 0) {
            return getClass().getName() + ":" + name;
        }
        return getClass().getName() + ":" + address.host + ":" + address.port;
    }
}
