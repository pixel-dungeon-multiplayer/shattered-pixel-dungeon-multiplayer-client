package io.github.pixeldungeonmultiplayer.shattered.client.network.scanners;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class RelaySD extends Thread implements ServiceDiscovery {

    private static final String CHARSET = "UTF-8";
    private static final int DELAY = 3000;
    public enum Protocol { V1, V2 }
    protected OutputStreamWriter writeStream;
    protected BufferedWriter writer;
    protected InputStreamReader readStream;
    private BufferedReader reader;
    protected Socket relaySocket;

    private List<ServerInfo> servers = new ArrayList<>();
    ServiceDiscoveryListener listener;
    private final Protocol protocol;
    private final String relayAddress;
    private final int relayPort;

    public RelaySD(Protocol protocol, String relayAddress, int relayPort) {
        this.protocol = protocol;
        this.relayAddress = relayAddress;
        this.relayPort = relayPort;
    }

    public static int getNewRelayPort(){
        if (!ShatteredPixelDungeon.useCustomRelay()){
            return SPDSettings.defaultRelayServerPort;
        }
        int port = SPDSettings.customRelayPort();
        return (port != 0)? port: SPDSettings.defaultRelayServerPort;
    }

    public static String getRelayAddress(){
        if (!ShatteredPixelDungeon.useCustomRelay()){
            return SPDSettings.defaultRelayServerAddress;
        }
        String address = SPDSettings.customRelayAddress();
        return (!"".equals(address))? address : SPDSettings.defaultRelayServerAddress;
    }

    public void run() {
        if (listener == null){
            return;
        }
        while (!Thread.currentThread().isInterrupted()) {
            Socket socket = null;
            try {
                socket = new Socket(relayAddress, relayPort);
            } catch (IOException e) {
                e.printStackTrace();
                GLog.h("relay thread stopped, no restart");
                return;
            }
            this.relaySocket = socket;
            try {
                writeStream = new OutputStreamWriter(
                        relaySocket.getOutputStream(),
                        Charset.forName(CHARSET).newEncoder()
                );
                readStream = new InputStreamReader(
                        relaySocket.getInputStream(),
                        Charset.forName(CHARSET).newDecoder()
                );
                reader = new BufferedReader(readStream);
                writer = new BufferedWriter(writeStream, 16384);

                while (true) {
                    try {
                        Thread.sleep(DELAY);
                        synchronized (writer) {
                            JSONObject get_request = new JSONObject();
                            get_request.put("action", protocol == Protocol.V2 ? "list_servers" : "get");
                            writer.write(get_request.toString());
                            writer.write('\n');
                            writer.flush();
                            String json = reader.readLine();
                            if (json == null) {
                                GLog.h("relay thread stopped, restarting");
                                socket.close();
                                break;
                            }
                            JSONObject servers_obj = new JSONObject(json);
                            updateServers(servers_obj);
                            if (protocol == Protocol.V2) {
                                socket.close();
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
                GLog.h("relay thread stopped,restarting");

            }
            catch(InterruptedException e) {
                GLog.h("relay thread stopped, no restart");
                return;
            }
        }
        GLog.h("relay thread stopped, no restart");
    }

    private void updateServers(JSONObject servers_obj) throws JSONException {
        JSONArray arr = servers_obj.getJSONArray("servers");
        List<ServerInfo> serverAddresses = new ArrayList<>(arr.length());
        for (int i = 0; i < arr.length(); i += 1) {
            JSONObject infoObj = arr.optJSONObject(i);
            if (infoObj == null) {
                break;
            }
            String name = JsonStringHelper.getString(infoObj, "name");
            int id = infoObj.getInt("id");
            JSONObject serverInfo = infoObj.optJSONObject("server_info");
            if (serverInfo == null) {
                serverInfo = infoObj;
            }
            RelayServerInfo info = new RelayServerInfo(
                    this,
                    protocol,
                    relayAddress,
                    relayPort,
                    id,
                    name,
                    serverInfo.optInt("players", 0),
                    serverInfo.optInt("max_players", 0),
                    serverInfo.optInt("challenges", 0) > 0
            );
            info.currentFloor = serverInfo.optInt("current_floor", 0);
            info.motd = serverInfo.optString("motd", null);
            info.serverVersion = JsonStringHelper.optString(infoObj, "server_version", serverInfo.optString("server_version", null));
            info.serverVersionCode = infoObj.optInt("server_version_code", serverInfo.optInt("server_version_code", 0));
            info.serverProtocolVersion = infoObj.optInt("server_protocol_version", serverInfo.optInt("server_protocol_version", 0));
            serverAddresses.add(info);
        }
        servers = serverAddresses;
        listener.onServiceFound(null);
    }

    public boolean started() {
        return !Thread.currentThread().isInterrupted();
    }

    protected void stopRelaySD() {
        this.interrupt();
        try {
            if (relaySocket != null) {
                relaySocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ServerInfo> getServerList() {
        return servers;
    }

    public int getPortForServerID(int id) {
        if (relaySocket == null){
            return 0;
        }
        if (!relaySocket.isConnected()){
            return 0;
        }
        try {
            synchronized (writer) {
                JSONObject get_request = new JSONObject();
                get_request.put("action", "connect");
                get_request.put("server", id);
                writer.write(get_request.toString());
                writer.write('\n');
                writer.flush();
                String json = reader.readLine();
                if (json == null) {
                    return 0;
                }
                JSONObject port_obj = new JSONObject(json);
                return port_obj.getInt("port");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean startDiscovery(ServiceDiscoveryListener listener) {
        this.listener = listener;
        start();
        return true;
    }

    @Override
    public boolean stopDiscovery(){
        stopRelaySD();
        return true;
    }
}
