package io.github.pixeldungeonmultiplayer.shattered.client.network.scanners;

import org.json.JSONObject;

import io.github.pixeldungeonmultiplayer.shattered.client.network.Protocol;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DirectServerParser {
    public static UserServerInfo fromAddress(InetSocketAddress address){
        try {
        Socket socket = new Socket();
        socket.connect(address);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        JSONObject object = new JSONObject(reader.readLine());
        if (Protocol.PACKET_HANDSHAKE.equals(object.optString(Protocol.FIELD_PACKET_TYPE, ""))
                && Protocol.NAME.equals(object.optString(Protocol.FIELD_PROTOCOL, ""))
                && object.optInt(Protocol.FIELD_VERSION, -1) >= Protocol.MIN_VERSION) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(new JSONObject().put(Protocol.FIELD_PACKET_TYPE, Protocol.PACKET_STATUS_REQUEST).toString());
            writer.write('\n');
            writer.flush();
            object = new JSONObject(reader.readLine());
        } else if (Protocol.PACKET_HANDSHAKE.equals(object.optString(Protocol.FIELD_PACKET_TYPE, ""))) {
            socket.close();
            return null;
        }
        socket.close();
        return new UserServerInfo(object, address, true);

        } catch (Exception ignored){

        }
        return null;
    }
}
