package io.github.pixeldungeonmultiplayer.shattered.client.network;


import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParserRegistry;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.DefaultActionParserRegistry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PointF;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static io.github.pixeldungeonmultiplayer.shattered.client.network.Client.disconnect;
import static java.lang.Thread.sleep;

public class ParseThread implements Callable<String> {

    public static final String CLIENT_TYPE = "SPD";

    @NotNull
    private final BufferedReader reader;
    @NotNull
    private final Socket socket;
    private static ParseThread activeThread;
    @NotNull
    private FutureTask<String> jsonCall;
    @NotNull
    private final ActionParserRegistry actionParsers;
    private boolean firstPacketReceived = false;

    private static boolean isOldServer = true;
    public static String serverUUID = null;
    public static int negotiatedProtocolVersion = Protocol.VERSION;

    public ParseThread(InputStreamReader readStream, Socket socket) {
        this(new BufferedReader(readStream), socket);
    }

    public ParseThread(@NotNull BufferedReader readStream, @NotNull Socket socket) {
        isOldServer = true;
        serverUUID = null;
        negotiatedProtocolVersion = Protocol.VERSION;
        this.socket = socket;
        this.reader = readStream;
        this.actionParsers = DefaultActionParserRegistry.create();
        activeThread = this;
        updateTask();
    }

    public static ParseThread getActiveThread() {
        if (activeThread == null) {
            return null;
        }
        if ((activeThread.socket == null) || (activeThread.socket.isClosed())) {
            return null;
        }
        return activeThread;
    }

    protected void updateTask() {
        if ((jsonCall == null) || (jsonCall.isDone())) {
            jsonCall = new FutureTask<String>(this);
            new Thread(jsonCall).start();
        }
    }

    @Override
    public String call() {
        if (socket.isClosed()) {
            return null;
        }
        try {
            return reader.readLine();
        } catch (IOException e) {
            Log.e("ParseThread", e.getMessage());
            return null;
        }
    }

    public void parseIfHasData() {
        String json = "";
        if (InterlevelScene.phase == InterlevelScene.Phase.FADE_OUT) {
            return;
        }
        if (jsonCall.isCancelled()) {
            disconnect();
            return;
        }
        if (!jsonCall.isDone()) {
            return;
        }
        try {
            json = jsonCall.get();
            updateTask();
            parse(json);
        } catch (IOException e) {
            GLog.n(e.getMessage());
            disconnect();
            return;
        } catch (InterruptedException e) {
            // disconnect will be upper
            return;
        } catch (JSONException e) {
            Log.w("parsing", e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException e) {
            {
                Log.w("parsing", e.getMessage());
                disconnect();
                return;
            }
        }

    }

    protected static void returnToMainScreen() {
        returnToMainScreen("Disconnected");
    }

    public static void returnToMainScreen(final String message) {
        Log.i("ParseThread", "parsing stopped");
        ShatteredPixelDungeon.switchScene(
                TitleScene.class,
                new Game.SceneChangeCallback() {
                    @Override
                    public void beforeCreate() {

                    }

                    @Override
                    public void afterCreate() {
                        ShatteredPixelDungeon.scene().add(new WndError(message));
                    }
                }

        );
    }

    private void parse(String json) throws IOException, JSONException, InterruptedException {
        if (json == null)
            throw new IOException("EOF");
        JSONObject data;
        try {
            data = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Parsing", "json: " + json);
            return;
        }
        if (DeviceCompat.isDebug()) {
            Log.i("Parsing", data.toString(4));
        }

        boolean isFirstPacket = !firstPacketReceived;
        firstPacketReceived = true;

        if (isDisconnectPacket(data)) {
            Client.disconnectWithoutSwitch();
            returnToMainScreen(data.optString("message", "Disconnected"));
            return;
        }

        if (isHandshakePacket(data)) {
            if (!isFirstPacket) {
                Log.w("Parsing", "Late handshake packet ignored");
                return;
            }
            if (!isCompatibleHandshake(data)) {
                Log.w("Parsing", "Unsupported handshake packet: " + data);
                Client.disconnectWithoutSwitch();
                returnToMainScreen("Unsupported server protocol");
                return;
            }
            Log.i("Parsing", "Server mode changed to SPDMP");
            isOldServer = false;
            negotiatedProtocolVersion = negotiatedProtocolVersion(data);
            serverUUID = JsonStringHelper.getString(data, Protocol.FIELD_SERVER_ID);
            Client.sendHeroClass(GamesInProgress.selectedClass);
            return;
        }

        if (data.has("server_type")){
            String serverType = JsonStringHelper.getString(data, "server_type");
            if (!CLIENT_TYPE.equals(serverType)) {
                throw new RuntimeException("Unsupported server");
                //todo
            }
            Log.i("Parsing", "Server mode changed to SPD");
            isOldServer = false;
        }
        if (!isConnectedToOldServer() && data.has("server_uuid")) {
            Gdx.app.log("ParseThread", "ServerUUID");
            serverUUID = JsonStringHelper.getString(data, "server_uuid");
            Client.sendHeroClass(GamesInProgress.selectedClass);
            throw new RuntimeException("Unsupported legacy server"); //todo
        }

        parseActionPacket(data);

    }

    private boolean isHandshakePacket(JSONObject data) {
        return Protocol.PACKET_HANDSHAKE.equals(data.optString(Protocol.FIELD_PACKET_TYPE, ""));
    }

    private boolean isDisconnectPacket(JSONObject data) {
        return Protocol.PACKET_DISCONNECT.equals(data.optString(Protocol.FIELD_PACKET_TYPE, ""));
    }

    private boolean isCompatibleHandshake(JSONObject data) {
        return isHandshakePacket(data)
                && Protocol.NAME.equals(data.optString(Protocol.FIELD_PROTOCOL, ""))
                && data.optInt(Protocol.FIELD_VERSION, -1) >= Protocol.MIN_VERSION
                && data.has(Protocol.FIELD_SERVER_ID);
    }

    private int negotiatedProtocolVersion(JSONObject data) {
        return Math.min(Protocol.VERSION, data.getInt(Protocol.FIELD_VERSION));
    }

    private void parseActionPacket(JSONObject data) throws JSONException {
        if (!Protocol.PACKET_ACTIONS_BATCH.equals(data.optString(Protocol.FIELD_PACKET_TYPE, ""))) {
            Log.w("Parsing", "Unsupported packet type: " + data.optString(Protocol.FIELD_PACKET_TYPE, ""));
            return;
        }
        if (data.has("actions")) {
            parseActions(data.getJSONArray("actions"));
        }
    }


    protected void parseActions(@NotNull JSONArray actions) {
        for (int i = 0; i < actions.length(); i++) {
            JSONObject actionObj;
            try {
                actionObj = actions.getJSONObject(i);
            } catch (JSONException e) {
                Log.wtf("ParseActions", "can't get action from array. " + e.toString());
                e.printStackTrace();
                continue;
            }
            String type = JsonStringHelper.optString(actionObj, "action_name", actionObj.optString("action_type"));
            parseAction(type, actionObj);
        }
    }

    private void parseAction(String type, JSONObject actionObj) {
        try {
            ActionParser parser = actionParsers.get(type, negotiatedProtocolVersion);
            if (parser == null) {
                GLog.h("unknown action type " + type + ". Ignored");
                return;
            }
            parser.parse(this, actionObj);
        } catch (JSONException e) {
            GLog.n("Incorrect action (" + type + "). Ignored");
            e.printStackTrace();
            Log.e("v2parser", actionObj.toString(4));
        }
    }

    @Deprecated
    public void parseDegradationAction(JSONObject actionObj) {
        try {
            //TODO: chceck if any of this is needed
            PointF point = new PointF((float) actionObj.getDouble("position_x"), (float) actionObj.getDouble("position_y"));
            JSONArray array = actionObj.getJSONArray("matrix");
            int[] matrix = new int[array.length()];
            for (int i = 0; i < matrix.length; i++) {
                matrix[i] = array.getInt(i);
            }
            int color = actionObj.optInt("color", Degradation.Speck.COLOR);
            GameScene.addGroup(new Degradation(point, matrix, color));
        } catch (JSONException e) {
            GLog.n("Incorrect degradation action " + e.getMessage());
        }
    }

    public static boolean isConnectedToOldServer(){
        return isOldServer;
    }

}
