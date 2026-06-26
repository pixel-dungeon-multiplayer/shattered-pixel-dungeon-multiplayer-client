package io.github.pixeldungeonmultiplayer.shattered.client.network.scanners;

import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ServerAddress;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;

public class UserServerInfo extends ServerInfo{
    public boolean online = false;
    private InetAddress IP;
    private int port;

    public UserServerInfo(String name, InetAddress ip, int port, int players, int maxPlayers, boolean haveChallenges) {
        this.name = name;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.IP = ip;
        this.port = port;
        this.haveChallenges = haveChallenges;
    }
    public UserServerInfo(JSONObject object, InetSocketAddress address){
        JSONObject serverInfo = object.getJSONObject("server_info");
        this.name = JsonStringHelper.getString(serverInfo, "name");
        this.currentFloor = serverInfo.getInt("current_floor");
        this.haveChallenges = serverInfo.getInt("challenges") > 0;
        this.IP = address.getAddress();
        this.port = address.getPort();
        this.motd = serverInfo.optString("motd", null);
        this.players = serverInfo.getInt("players");
        this.maxPlayers = serverInfo.getInt("max_players");
    }
    public UserServerInfo(JSONObject object, InetSocketAddress address, boolean online){
        this(object, address);
        this.online = online;
    }
    @Override
    public ServerAddress getAddress() {
        ServerAddress address = new ServerAddress();
        address.host = IP.getHostAddress();
        address.port = port;
        return address;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof UserServerInfo)) return false;

        UserServerInfo that = (UserServerInfo) o;
        return port == that.port && Objects.equals(IP, that.IP);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(IP);
        result = 31 * result + port;
        return result;
    }

    @Override
    public int icon() {
        return online ? ItemSpriteSheet.MAGES_STAFF : ItemSpriteSheet.BROKEN_STAFF;
    }
}
