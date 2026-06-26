package io.github.pixeldungeonmultiplayer.shattered.client.network.scanners;

import io.github.pixeldungeonmultiplayer.shattered.client.network.Client;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ServerAddress;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;

public class RelayServerInfo extends ServerInfo {
    private int id;
    private String relayAddress;
    private int relayPort;
    private RelaySD.Protocol protocol;
    private RelaySD relay;

    public RelayServerInfo(RelaySD relay, RelaySD.Protocol protocol, String relayAddress, int relayPort, int id, String name, int players, int maxPlayers, boolean haveChallenges) {
        this.relay = relay;
        this.protocol = protocol;
        this.relayAddress = relayAddress;
        this.relayPort = relayPort;
        this.id = id;
        this.name = name;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.haveChallenges = haveChallenges;
    }

    @Override
    public ServerAddress getAddress() {
        if (protocol == RelaySD.Protocol.V2) {
            return null;
        }
        int port = relay.getPortForServerID(id);
        if (port == 0){
            return null;
        }
        ServerAddress address = new ServerAddress();
        address.host = relayAddress;
        address.port = port;
        return address;
    }

    @Override
    public boolean connect() {
        if (protocol == RelaySD.Protocol.V1) {
            return super.connect();
        }
        try {
            Socket socket = new Socket(relayAddress, relayPort);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(),
                    Charset.forName(Client.CHARSET).newEncoder()
            ));
            JSONObject request = new JSONObject();
            request.put("action", "connect_server");
            request.put("server_id", id);
            writer.write(request.toString());
            writer.write('\n');
            writer.flush();
            return Client.connect(socket);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int icon() {
        return protocol == RelaySD.Protocol.V2 ? ItemSpriteSheet.BEACON : ItemSpriteSheet.CHEST;
    }
}
