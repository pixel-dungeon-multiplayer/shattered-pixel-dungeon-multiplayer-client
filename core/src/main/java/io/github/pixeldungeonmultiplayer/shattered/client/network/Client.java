package io.github.pixeldungeonmultiplayer.shattered.client.network;


import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import io.github.pixeldungeonmultiplayer.shattered.client.network.scanners.ServerInfo;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;


public class Client extends Thread {
    public static final String CHARSET = "UTF-8";

    protected static OutputStreamWriter writeStream;
    protected static BufferedWriter writer;
    protected static InputStreamReader readStream;
    protected static Socket socket = null;
    protected static Client client;
    protected static ParseThread parceThread = null;
    protected static final NetworkPacket packet = new NetworkPacket();
    protected static final int BUFFER_SIZE = 16 * 1024; // bytes

    public static boolean connect(ServerInfo server) {
        return server.connect();
    }

    public static boolean connect(String server, int port) {
        packet.clearData();
        try {
            return connect(new Socket(server, port));
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean connect(Socket connectedSocket) {
        packet.clearData();
        try {
            socket = connectedSocket;
            writeStream = new OutputStreamWriter(
                    socket.getOutputStream(),
                    Charset.forName(CHARSET).newEncoder()
            );
            readStream = new InputStreamReader(
                    socket.getInputStream(),
                    Charset.forName(CHARSET).newDecoder()
            );
            writer = new BufferedWriter(writeStream, BUFFER_SIZE);
            com.shatteredpixel.shatteredpixeldungeon.journal.Document.reset();
            com.shatteredpixel.shatteredpixeldungeon.journal.Notes.reset();
            parceThread = new ParseThread(readStream, socket);
            client = new Client();
            client.setDaemon(true);
            client.start();
            return socket.isConnected();
        } catch (IOException e) {
            return false;
        }
    }

    public static void disconnect() {
        disconnectWithoutSwitch();
        ParseThread.returnToMainScreen();
    }

    public static void disconnectWithoutSwitch() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket = null;
        readStream = null;
        writeStream = null;
    }

    public void run() {
        if (!socket.isConnected()) {
            disconnect();
            return;
        }
        try {
            while ((socket != null) && (!socket.isClosed())) sleep(1000);
        } catch (Exception e) {
            GLog.n(e.getStackTrace().toString());
        }
        if (socket != null) {
            disconnect();
        }
    }

    public static void flush() {
        try {
            synchronized (packet.dataRef) {
                synchronized (writeStream) {
                    if (packet.dataRef.get().length() == 0) {
                        return;
                    }
                    if (!ParseThread.isConnectedToOldServer()
                            && !packet.dataRef.get().has(Protocol.FIELD_PACKET_TYPE)) {
                        packet.dataRef.get().put(Protocol.FIELD_PACKET_TYPE, Protocol.PACKET_CLIENT_COMMAND);
                    }
                    writer.write(packet.dataRef.get().toString());
                    writer.write('\n');
                    writer.flush();
                    packet.clearData();
                }
            }
        } catch (IOException e) {
            GLog.h("IOException. Message: {0}", e.getMessage());
            disconnect();
        }
    }

    //methods
    public static void sendHeroClass(HeroClass heroClass) {
        packet.packAndAddHeroClass(heroClass.name().toLowerCase());
        flush();
    }
}
