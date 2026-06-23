package com.shatteredpixel.shatteredpixeldungeon.network;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.nikita22007.pixeldungeonmultiplayer.JavaUtils;
import com.nikita22007.pixeldungeonmultiplayer.TextureManager;
import com.nikita22007.pixeldungeonmultiplayer.TranslationUtils;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CustomBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CustomMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CustomTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ActionParserRegistry;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.DefaultActionParserRegistry;
import com.shatteredpixel.shatteredpixeldungeon.plants.CustomPlant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.FadingTraps;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.legacy.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.legacy.WndTradeItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.legacy.WndWandmaker;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PointF;
import com.watabou.utils.Reflection;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static com.nikita22007.pixeldungeonmultiplayer.JavaUtils.hasNotNull;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;
import static com.shatteredpixel.shatteredpixeldungeon.network.Client.disconnect;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite.spriteClassFromName;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite.spriteFromClass;
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
        }

        if (isConnectedToOldServer()) {
            parseLegacy(data);
        } else {
            parseActionPacket(data);
        }
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

    private void parseLegacy(JSONObject data) throws JSONException, InterruptedException {
        if (data.has("level_params")){
            parseLevelParams(data.getJSONObject("level_params"));
        }

        //Log.w("data", data.toString(4));
        for (Iterator<String> it = data.keys(); it.hasNext(); ) {
            String token = it.next();
            switch (token) {
                case "server_type": {
                    //already parsed
                    break;
                }
                case "texturepack":
                {
                    try {
                        TextureManager.INSTANCE.loadTexturePack(JavaUtils.InputStreamFromBase64(JsonStringHelper.getString(data, token)));
                    }catch (IOException err){
                        ShatteredPixelDungeon.scene().add(new WndError("Malformed texture pack"));
                        }
                    break;
                }
                case "server_actions": {
                    parseServerActions(data.getJSONArray(token));
                    break;
                }
                //level block
                case "map": {
                    parseLevel(data.getJSONObject(token));
                    break;
                }
                case "level_params":
                {
                    //parseLevelParams(data.getJSONObject(token));
                    //parsed before
                    break;
                }
                //UI block
                case "interlevel_scene": {
                    //todo can cause crash
                    JSONObject ilsObj = data.getJSONObject(token);
                    if (ilsObj.has("state")) {
                        String stateName = JsonStringHelper.getString(data.getJSONObject(token), "state").toUpperCase();
                        InterlevelScene.Phase phase = InterlevelScene.Phase.valueOf(stateName);
                        InterlevelScene.phase = phase;
                    }
                    if (ilsObj.has("type")) {
                        String modeName = JsonStringHelper.getString(ilsObj, "type").toUpperCase(Locale.ENGLISH);
                        if (modeName.equals("CUSTOM")) {
                            modeName = "NONE";
                        }
                        InterlevelScene.Mode mode = InterlevelScene.Mode.valueOf(modeName);
                        InterlevelScene.mode = mode;
                    }

                    InterlevelScene.reset_level = ilsObj.optBoolean("reset_level");

                    if (ilsObj.has("message")) {
                        InterlevelScene.customMessage = JsonStringHelper.getString(ilsObj, "message");
                    }
                    if (!(Game.scene() instanceof InterlevelScene)) {
                        if (!((Game.scene() instanceof GameScene) && (InterlevelScene.phase == InterlevelScene.Phase.FADE_OUT))) {
                            Game.switchScene(InterlevelScene.class);
                        }
                    }
                    break;
                }
                case "actors": {
                    parseActors(data.getJSONArray(token));
                    break;
                }
                case "buffs": {
                    parseBuffs(data.getJSONArray(token));
                    break;
                }
                case "hero": {
                    parseHero(data.getJSONObject(token));
                    break;
                }
                case "actions": {
                    parseLegacyActions(data.getJSONArray(token));
                    break;
                }
                case "messages": {
                    parseMessages(data.getJSONArray(token));
                    break;
                }
                case "inventory": {
                    parseInventory(data.getJSONObject(token));
                    break;
                }
                case "heaps": {
                    try {
                        JSONArray heaps = data.getJSONArray(token);
                        for (int i = 0; i < heaps.length(); i++) {
                            parseHeap(heaps.getJSONObject(i));
                        }
                        break;
                    } catch (JSONException e) {
                        Log.e("parseThread", String.format("incorrect heap array. Ignored. Exception: %s ", e.getMessage()));
                    }
                    break;
                }
                case "window": {
                    parseWindow(data.getJSONObject(token));
                    break;
                }
                case "ui": {
                    parseUI(data.getJSONObject(token));
                    break;
                }
                case "plants": {
                    parsePlants(data.getJSONArray(token));
                    break;
                }
                case "traps":
                    parseTraps(data.getJSONArray(token));
                    break;
                case "server_uuid": {
                    Gdx.app.log("ParseThread", "ServerUUID");
                    serverUUID = JsonStringHelper.getString(data, "server_uuid");
                    Client.sendHeroClass(GamesInProgress.selectedClass);
                    break;
                }
                case "redirect": {
                    Client.disconnectWithoutSwitch();
                    ShatteredPixelDungeon.switchScene(InterlevelScene.class);
                    JSONObject redirectObject = data.getJSONObject(token);
                    if (redirectObject.has("uuid")){
                        NetworkPacket.redirectUUID = JsonStringHelper.getString(redirectObject, "uuid");
                    }
                    if(redirectObject.has("password")){
                        NetworkPacket.password = JsonStringHelper.getString(redirectObject, "password");
                    }
                    Client.connect(JsonStringHelper.getString(redirectObject, "host"), redirectObject.getInt("port"));
                    break;
                }
                case "server_info": {
                    //Doesn't need to be parsed
                    break;
                }
                default: {
                    GLog.h("Incorrect packet token: \"%s\". Ignored", token);
                    continue;
                }
            }
        }

    }


    public void parsePlant(JSONObject plantObject) throws JSONException {
        if (plantObject.isNull("plant_info")) {
            if (level == null || level.plants == null) {
                return;
            }
            Plant plant = level.plants.get(plantObject.getInt("pos"));
            if (plant != null) {
                plant.wither();
            }
            return;
        }
        JSONObject plantInfo = plantObject.optJSONObject("plant_info");
        Plant.Seed seed = new CustomPlant.Seed(plantInfo);
        level.plant(seed, plantObject.getInt("pos"));
    }

    public void parsePlants(JSONArray plantsArray) {
        for (int i = 0; i < plantsArray.length(); i++) {
            JSONObject plantObject = plantsArray.optJSONObject(i);
            if (plantObject == null) {
                Log.e("Parse Thread", "malformed plant.");
                continue;
            }
            try {
                parsePlant(plantObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void parseTrap(JSONObject trapObject) throws JSONException {
        if (trapObject.isNull("trap_info")) {
            if (level == null || level.traps == null) {
                return;
            }
            int pos = trapObject.getInt("pos");
            level.traps.remove(pos);
            GameScene.updateMap(pos);
            return;
        }
        Trap trap = new CustomTrap(trapObject);
        level.setTrap(trap, trapObject.getInt("pos"));
    }

    public void parseTraps(JSONArray trapsArray) {
        for (int i = 0; i < trapsArray.length(); i++) {
            JSONObject trapObject = trapsArray.optJSONObject(i);
            if (trapObject == null) {
                Log.e("Parse Thread", "malformed trap.");
                continue;
            }
            try {
                parseTrap(trapObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseUI(JSONObject uiObject) {
        if (uiObject.has("depth")) {
            Dungeon.depth = uiObject.optInt("depth");
        }
        if (uiObject.has("iron_keys_count") || uiObject.has("iron_key_count")) {
            if (isConnectedToOldServer()) {
                JSONArray ironKeys = new JSONArray();
                ironKeys.put(0);
                ironKeys.put(uiObject.optInt("iron_keys_count", uiObject.optInt("iron_key_count", -20)));
                ironKeys.put(0);
                ironKeys.put(0);
                ironKeys.put(0);
                GameScene.updateKeyDisplay(ironKeys);
            } else {
                GameScene.updateKeyDisplay(uiObject.getJSONArray("iron_keys_count"));
            }
        }
        if (uiObject.has("resume_button_visible")) {
            //TODO: might remove this
            //hero.resume_button_visible = uiObject.optBoolean("resume_button_visible");
        }
        if (uiObject.has("cell_listener_prompt")) {
            GameScene.defaultCellListener.setCustomPrompt(uiObject.optString("cell_listener_prompt", null));
        }
        if (uiObject.has("attack_indicator_target")){
            AttackIndicator.setCandidates(uiObject.getInt("attack_indicator_target"));
        }
        if(uiObject.has("counter")){
            Scene scene = Game.scene();
            if (scene instanceof GameScene){
                ((GameScene) scene).setCounter((float) uiObject.getDouble("counter"));
            }
        }
    }

    public void parseWindow(JSONObject windowObj) {
        try {
            int id = windowObj.getInt("id");
            String type = JsonStringHelper.getString(windowObj, "type");
            JSONObject args = windowObj.optJSONObject("params");
            if (args == null) {
                args = windowObj.optJSONObject("args");
            }
            switch (type) {
                case "message":
                case "wnd_message": {
                    GameScene.show(new WndMessage(id, JsonStringHelper.getString(args, "text")));
                    break;
                }
                case "option":
                case "wnd_option": {
                    GameScene.show(new WndOptions(id, args));
                    break;
                }
                case "bag":
                case "wnd_bag": {
                    String title = JsonStringHelper.getString(args, "title");
                    boolean has_listener = args.getBoolean("has_listener");
                    JSONArray allowed_items = args.optJSONArray("allowed_items");
                    JSONArray last_bag_path = args.optJSONArray("last_bag_path"); // todo
                    if (Game.scene() instanceof GameScene) {
                        GameScene.show(new WndBag(id, hero.belongings.backpack, has_listener, allowed_items, title));
                    } else {
                        Game.scene().addToFront(new WndBag(id, hero.belongings.backpack, has_listener, allowed_items, title));
                    }
                    break;
                }
                case "info_cell":
                    GameScene.show(new WndInfoCell(windowObj.getJSONObject("args")));
                    break;
                case "quest":
                    GameScene.show(new WndQuest(id, windowObj.getJSONObject("args")));
                    break;
                case "sad_ghost":
                    GameScene.show(new WndSadGhost(id, windowObj.getJSONObject("args")));
                    break;
                case "wandmaker":
                    GameScene.show(new WndWandmaker(windowObj));
                    break;
                case "trade_item":
                    GameScene.show(new WndTradeItem(windowObj));
                    break;
                case "guess":
                    GameScene.show(new StoneOfIntuition.WndGuess(windowObj));
                    break;
                default: {
                    Log.e("parse_window", String.format("incorrect window type: %s", type));
                }
            }
        } catch (JSONException e) {
            Log.e("parse_window", String.format("bad_window. %s", e.getMessage()));
        } catch (NullPointerException e) {
            Log.e("parse_window", String.format("bad_window. %s", e.getMessage()));
        }
    }

    public void parseServerActions(JSONArray server_actions_arr) {
        JSONObject debug_action = null;
        for (int i = 0; i < server_actions_arr.length(); i += 1) {
            try {
                debug_action = server_actions_arr.getJSONObject(i);
                parseServerAction(server_actions_arr.getJSONObject(i));
                debug_action = null;
            } catch (JSONException e) {
                String message;
                if (debug_action == null) {
                    message = String.format("can't get action with id:  %d", i);
                } else {
                    try {
                        message = String.format("malformed_action:  %s", debug_action.toString(2));
                    } catch (JSONException e1) {
                        message = String.format("malformed_action. Can't get string. Exception: %s", e1.getMessage());
                    }
                }
                message += String.format("Exception: %s", e.getMessage());
                Log.e("parse_server_actions", message);
            }
        }
    }

    private void parseServerAction(JSONObject action_object) throws JSONException {
        switch (JsonStringHelper.getString(action_object, "type")) {
            case "reset_level": {
                Level old_level = level;
                level = new SewerLevel();
                level.create(old_level);
                break;
            }
            default:
                Log.e("parse_server_actions", String.format("unknown_action  %s", JsonStringHelper.getString(action_object, "type")));
        }
    }

    public void parseHeap(JSONObject heapObj) {
        try {
            throw new RuntimeException("fix this legacy");
        } catch (JSONException e) {
            Log.e("parse heap", String.format("bad heap. Exception: %s", e.getMessage()));
        }
    }

    public void parseMessage(JSONObject messageObj) {
        Scene scene = Game.scene();
        if (!(scene instanceof GameScene)) {
            return;
        }
        GameLog log = ((GameScene) scene).getGameLog();
        try {
            if (messageObj.has("color")) {
                log.WriteMessage(JsonStringHelper.getString(messageObj, "text"), messageObj.getInt("color"));
            } else {
                log.WriteMessageAutoColor(JsonStringHelper.getString(messageObj, "text"));
            }
        } catch (JSONException e) {
            Log.w("ParseThread", "Incorrect message");
        }
    }

    public void parseMessages(JSONArray messages) {
        for (int i = 0; i < messages.length(); i++) {
            try {
                parseMessage(messages.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseBadge(JSONObject badgeObj) {
        // Implementation for badge notifications
        Log.i("ParseThread", "Badge unlocked: " + badgeObj.optString("name"));
    }

    public void parseInventoryDefineSpecialSlots(JSONArray slotsArr) {
        Log.w("ParseThread", "inventory_define_special_slots received, but dynamic slots are not implemented on client yet. Slots count: " + slotsArr.length());
    }

    public void parseItemAction(List<Integer> path, JSONObject itemObj, String mode) throws JSONException {
        Belongings belongings = hero.belongings;
        switch (mode) {
            case "place":
            case "add": {
                Item item = itemObj != null ? CustomItem.createItem(itemObj) : null;
                belongings.putItemIntoSlot(path, item, false);
                break;
            }
            case "remove": {
                belongings.removeItemFromSlot(path);
                break;
            }
            case "update": {
                CustomItem item = ((CustomItem) belongings.getItemInSlot(path));
                if (item != null && itemObj != null) {
                    item.update(itemObj);
                } else if (itemObj != null) {
                    CustomItem newItem = CustomItem.createItem(itemObj);
                    belongings.putItemIntoSlot(path, newItem, true);
                }
                break;
            }
            case "replace": {
                Item item = itemObj != null ? CustomItem.createItem(itemObj) : null;
                belongings.putItemIntoSlot(path, item, true);
                break;
            }
        }
    }

    public void parseInventory(JSONObject inv) {
        if (inv.has("backpack")) {
            try {
                hero.belongings.backpack = new Belongings.Backpack(inv.getJSONObject("backpack"));
            } catch (JSONException e) {
                Log.e("ParseThread", String.format("Can't parse backpack. Stacktrace: %s", e.toString()));
            }
        }
        if (inv.has("special_slots")) {
            JSONArray slotsArr;
            try {
                slotsArr = inv.getJSONArray("special_slots");
            } catch (JSONException ignored) {
                assert false : "wtf";
                slotsArr = new JSONArray();
            }
            try {
                //throw new RuntimeException("unreleased"); //todo remove it?

                for (int i = 0; i < slotsArr.length(); i++) {
                    JSONObject slotObj = slotsArr.getJSONObject(i);
                    CustomItem item = null;
                    int id = -1;
                    if (slotObj.has("id")) {
                            id = slotObj.getInt("id");
                    }
                    if (slotObj.has("sprite")) {
                        //ignored, we already know sprite
                        //slot.sprite = slotObj.getString("sprite");
                    }
                    if (slotObj.has("image_id")) {
                        //ignored, we already know image
                        //slot.image_id = slotObj.getInt("image_id");
                    }
                    if (slotObj.has("item")) {
                        if (slotObj.isNull("item")) {
                            item = null;
                        } else {
                            item = CustomItem.createItem(slotObj.getJSONObject("item"));
                        }
                    }
                    hero.belongings.updateSpecialSlot(item, id);
                }

            } catch (JSONException e) {
                Log.w("ParseThread", "Can't parse slot");
            }
        }
    }

    public void parseSpriteAction(JSONObject actionObj) throws JSONException {
        int actorID = actionObj.getInt("actor_id");
        Actor actor = Actor.findById(actorID);
        if (actor == null) {
            GLog.h("solve actor");
            return;
        }
        CharSprite sprite = ((Char) actor).sprite;
        if (sprite == null) {
            GLog.h("actor " + actorID + "has null sprite");
            return;
        }

        sprite.parseAction(actionObj);
    }

    protected void parseActions(@NotNull JSONArray actions) {
        ArrayList<JSONObject> spriteActions = new ArrayList<>();
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
            if ("sprite_action".equals(type)) {
                spriteActions.add(actionObj);
                continue;
            }
            parseAction(type, actionObj);
        }
        for (JSONObject actionObj : spriteActions) {
            parseAction(JsonStringHelper.optString(actionObj, "action_name", actionObj.optString("action_type")), actionObj);
        }
    }

    protected void parseLegacyActions(@NotNull JSONArray actions) {
        for (int i = 0; i < actions.length(); i++) {
            JSONObject actionObj;
            try {
                actionObj = actions.getJSONObject(i);
            } catch (JSONException e) {
                Log.wtf("ParseActions", "can't get action from array. " + e.toString());
                e.printStackTrace();
                continue;
            }
            String type = JsonStringHelper.optString(actionObj, "action_type", actionObj.optString("action_name"));
            try {
                parseLegacyAction(type, actionObj);
            } catch (JSONException e) {
                GLog.n("Incorrect action ( " + type + "). Ignored");
                e.printStackTrace();
            }
        }
    }

    private void parseLegacyAction(String type, JSONObject actionObj) throws JSONException {
        switch (type) {
            case "sprite_action": {
                parseSpriteAction(actionObj);
                break;
            }
            case "add_item_to_bag": {
                JSONObject itemObj = actionObj.optJSONObject("item");
                parseItemAction(parseIntegerList(actionObj.getJSONArray("slot")), itemObj, actionObj.optString("update_mode"));
                break;
            }
            case "show_status": {
                parseShowStatusAction(actionObj);
                break;
            }
            case "degradation": {
                parseDegradationAction(actionObj);
                break;
            }
            case "show_banner":
            case "visual_show_banner": {
                parseBannerShowAction(actionObj);
                break;
            }
            case "lightning_visual": {
                parseLightningVisualAction(actionObj);
                break;
            }
            case "death_ray_centered_visual": {
                parseDeathRayCenteredVisualAction(actionObj);
                break;
            }
            case "wound_visual": {
                parseWoundVisualAction(actionObj);
                break;
            }
            case "ripple_visual": {
                parseRippleVisualAction(actionObj);
                break;
            }
            case "missile_sprite_visual": {
                parseMissileSpriteVisualAction(actionObj);
                break;
            }
            case "checked_cell_visual": {
                if (Dungeon.level.heroFOV[actionObj.getInt("pos")]) {
                    GameScene.effect(new CheckedCell(actionObj.getInt("pos")));
                }
                break;
            }
            case "play_sample": {
                Sample.INSTANCE.play(actionObj);
                break;
            }
            case "music": {
                Music.INSTANCE.parseAction(actionObj).execute();
                break;
            }
            case "load_sample": {
                Sample.INSTANCE.load(actionObj.getJSONArray("samples"));
                break;
            }
            case "unload_sample": {
                Sample.INSTANCE.unload(JsonStringHelper.getString(actionObj, "sample"));
                break;
            }
            case "shake_camera": {
                Camera.main.shake((float) actionObj.getDouble("magnitude"), (float) actionObj.getDouble("duration"));
                break;
            }
            case "enchanting_visual": {
                int targetCharId = actionObj.getInt("target");
                Actor actor = Actor.findById(targetCharId);
                if (!(actor instanceof Char)) {
                    GLog.n("Enchanting: Can't find char with id " + targetCharId + ". Ignored");
                    break;
                }
                Item item = CustomItem.createItem(actionObj.getJSONObject("item"));
                Enchanting.show((Char) actor, item);
                break;
            }
            case "flare_visual": {
                PointF position;
                if (actionObj.has("pos")) {
                    position = DungeonTilemap.tileCenterToWorld(actionObj.getInt("pos"));
                } else {
                    position = new PointF(
                            (float) actionObj.getDouble("position_x"),
                            (float) actionObj.getDouble("position_y")
                    );
                }

                Flare flare = new Flare(
                        actionObj.getInt("rays"),
                        (float) actionObj.getDouble("radius")
                );
                flare.angle = (float) actionObj.optDouble("angle", 45);
                flare.angularSpeed = (float) actionObj.optDouble("angular_speed", 180);
                flare.color(actionObj.getInt("color"), actionObj.optBoolean("light_moode", true));
                GameScene.showFlare(flare, position, (float) actionObj.getDouble("duration"));
                break;
            }
            case "emitter_visual": {
                parseEmitterVisualAction(actionObj);
                break;
            }
            case "emitter_decor": {
                level.addVisual(actionObj);
                break;
            }
            case "heap_drop_visual": {
                parseHeadDropVisualAction(actionObj);
                break;
            }
            case "magic_missile_visual": {
                parseMagicMissileVisual(actionObj);
                break;
            }
            case "spell_sprite": {
                ShowSpellSprite(actionObj);
                break;
            }
            case "discover_tile": {
                GameScene.discoverTile(
                        actionObj.getInt("pos"),
                        actionObj.getInt("old_tile")
                );
                break;
            }
            case "surprise_visual": {
                Surprise.hit(actionObj.getInt("pos"), actionObj.getInt("angle"));
                break;
            }
            case "boss_health_bar": {
                BossHealthBar.parseAction(actionObj);
                break;
            }
            case "game_scene_flash": {
                GameScene.flash(actionObj.getInt("color"), actionObj.getBoolean("light"));
                break;
            }
            case "fading_traps": {
                FadingTraps.fromJSON(actionObj);
                break;
            }
            default:
                GLog.h("unknown action type " + type + ". Ignored");
        }
    }

    private List<Integer> parseIntegerList(JSONArray array) throws JSONException {
        List<Integer> result = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            result.add(array.getInt(i));
        }
        return result;
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
        }
    }

    public void ShowSpellSprite(JSONObject actionObj) throws JSONException {
        Actor actor = Actor.findById(actionObj.getInt("target"));
        Char chr = actor instanceof Char? (Char) actor: null;
        SpellSprite.show(
                chr,
                actionObj.getInt("spell")
                );
    }

    public void parseMagicMissileVisual(JSONObject actionObj) throws JSONException{
        int from = actionObj.getInt("from");
        int to = actionObj.getInt("to");
        Char actor = Actor.findChar(from);
        Group group = null;
        if ((actor != null) && (actor.sprite != null))
        {
            group = actor.sprite.parent;
        }
        if (isConnectedToOldServer()) {
            MagicMissile.show(JsonStringHelper.getString(actionObj, "type"), from, to, group);
        } else {
            MagicMissile.show(actionObj.getInt("type"), from, to, group);
        }
    }

    public void parseHeadDropVisualAction(JSONObject actionObj) throws JSONException {
        //int from =actionObj.getInt("from");
        int to = actionObj.getInt("to");
        //Item item = CustomItem.createItem(actionObj.getJSONObject("item"));
        //ItemSprite itemSprite = new ItemSprite(item);
        //PixelDungeon.scene().add(itemSprite);
        //itemSprite.drop(from, to);
        Heap heap = level.heaps.get(to);
        if (heap == null)
        {
            return;
        }
        heap.sprite.drop();
    }

    public void parseShowStatusAction(JSONObject actionObj) throws JSONException {
        float x = (float) actionObj.getDouble("x");
        float y = (float) actionObj.getDouble("y");
        Integer key = actionObj.has("key") ? actionObj.getInt("key") : null;
        String text = JsonStringHelper.getString(actionObj, "text");
        int color = actionObj.getInt("color");
        boolean ignore_position = actionObj.optBoolean("ignore_position", true);
        if ((key != null) && ignore_position) {
            Char ch = Actor.findChar(key);
            if ((ch != null) && (ch.sprite != null)) {
                ch.sprite.showStatus(color, text);
                return;
            }
        }
        if (key == null) {
            FloatingText.show(x, y, text, color);
        } else {
            FloatingText.show(x, y, key, text, color);
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

    //FIXME
    public void parseBannerShowAction(JSONObject actionObj) {
        try {
            BannerSprites.Type bannerType = BannerSprites.Type.valueOf(JsonStringHelper.getString(actionObj, actionObj.getString("banner").toUpperCase()));

            Banner banner = new Banner(BannerSprites.get(bannerType));
            banner.show(actionObj.getInt("color"), (float) actionObj.getDouble("fade_time"), (float) actionObj.getDouble("fade_time"));
            GameScene.showBannerStatic(banner);
        } catch (JSONException e) {
            GLog.n("Incorrect BannerShowAction action " + e.getMessage());
        }
    }


    public void parseLightningVisualAction(JSONObject actionObj) {
        try {
            JSONArray cellsJson = actionObj.getJSONArray("cells");
            int[] cells = new int[cellsJson.length()];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = cellsJson.getInt(i);
            }
            GameScene.addGroup(new PDLightning(cells, cells.length, null));

        } catch (JSONException e) {
            GLog.n("Incorrect LightningVisualAction action " + e.getMessage());
        }
    }

    public void parseDeathRayCenteredVisualAction(JSONObject actionObj) {
        try {
            GameScene.effect(new Beam.DeathRay(actionObj.getInt("start"), actionObj.getInt("stop"), (float) actionObj.getDouble("duration")));
        } catch (JSONException e) {
            GLog.n("Incorrect DeathRayCenteredVisualAction action " + e.getMessage());
        }
    }

    public void parseWoundVisualAction(JSONObject actionObj) {
        try {
            Wound.hitWithTimeToFade(actionObj.getInt("pos"), (float) actionObj.getDouble("duration"));
        } catch (JSONException e) {
            GLog.n("Incorrect WoundVisualAction action " + e.getMessage());
        }
    }

    public void parseRippleVisualAction(JSONObject actionObj) {
        try {
            GameScene.ripple(actionObj.getInt("pos"));
        } catch (JSONException e) {
            GLog.n("Incorrect RippleVisualAction action " + e.getMessage());
        }
    }

    public void parseMissileSpriteVisualAction(JSONObject actionObj) {
        try {
            MissileSprite.show(actionObj);
        } catch (JSONException e) {
        }
    }

    public void parseEmitterVisualAction(JSONObject actionObj) {
        try {
            if(actionObj.has("kill") && actionObj.getBoolean("kill")) {
                int id = actionObj.getInt("id");
                Emitter emitter = Emitter.infiniteEmitters.get(id);
                if (emitter != null) {
                    emitter.killAndErase();
                } else {
                    GLog.n("Failed to find emitter");
                }

                return;
            }
            Char target = null;

            boolean fillTarget = true;
            PointF position = null;
            PointF shift = null;
            float width;
            float height;
            float interval;
            int quantity;

            Emitter.Factory factory = null;

            if (actionObj.has("target_char")) {
                fillTarget = actionObj.optBoolean("fill_target", true);
                int targetCharId = actionObj.getInt("target_char");
                Actor targetActor = Actor.findById(targetCharId);
                if (targetActor instanceof Char) {
                    target = (Char) targetActor;
                    if (!target.sprite.visible) {
                        return;
                    }
                } else {
                    GLog.n("Incorrect EmitterVisualAction action: target is not char");
                }
            }

            if (actionObj.has("pos")) {
                if (!Dungeon.level.heroFOV[actionObj.getInt("pos")]) {
                    return;
                }
                position = DungeonTilemap.tileToWorld(actionObj.getInt("pos"));
            } else if (actionObj.has("position_x")) {
                position = new PointF(
                        (float) actionObj.getDouble("position_x"),
                        (float) actionObj.getDouble("position_y")
                );
            }

            if (actionObj.has("shift_x")) {
                shift = new PointF(
                        (float) actionObj.getDouble("shift_x"),
                        (float) actionObj.getDouble("shift_y")
                );
                if (position != null) {
                    if ((shift.x != 0) || (shift.y != 0)) {
                        position.x += shift.x;
                        position.y += shift.y;
                    }
                }
            }

            width = (float) actionObj.getDouble("width");
            height = (float) actionObj.getDouble("height");

            interval = (float) actionObj.getDouble("interval");
            quantity = actionObj.getInt("quantity");

            factory = emitterFactoryFromJSONObject(actionObj.getJSONObject("factory"));
            if (factory == null) {
                return;
            }
            Emitter emitter = GameScene.emitter();
            if (emitter == null) {
                return;
            }
            if ((target == null) && (position == null)) {
                GLog.n("Incorrect EmitterVisualAction action: no any target or position");
                return;
            }
            if ((target != null) && (shift != null)) {
                if ((shift.x != 0) || (shift.y != 0)) {
                    position = new PointF(
                            target.sprite.x + shift.x,
                            target.sprite.y + shift.y
                    );
                    target = null;
                }
            }
            if (target != null) {
                emitter.pos(target.sprite);
            } else {
                emitter.pos(position);
            }
            emitter.width = width;
            emitter.height = height;
            emitter.fillTarget = fillTarget;
            emitter.start(factory, interval, quantity);
            if(actionObj.has("id")){
                emitter.id = actionObj.getInt("id");
                Emitter.infiniteEmitters.put(emitter.id,emitter);
            }
        } catch (JSONException e) {
            GLog.n("Incorrect EmitterVisualAction action: " + e.getMessage());
        }
    }

    protected Emitter.Factory emitterFactoryFromJSONObject(JSONObject factoryObj) throws JSONException {
        if (!isConnectedToOldServer() && factoryObj.has("path")) {
            Emitter.Factory factory = null;
            try {
                Class<?> factoryClass = ClassReflection.forName(JsonStringHelper.getString(factoryObj, "path"));
                Constructor<?> contructor;
                try {
                    contructor = factoryClass.getDeclaredConstructor(JSONObject.class);
                } catch (Exception ignored) {
                    try {
                        contructor = factoryClass.getDeclaredConstructor();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                contructor.setAccessible(true);
                if (contructor.getParameterTypes().length > 0) {
                    factory = (Emitter.Factory) contructor.newInstance(factoryObj);
                } else {
                    factory = (Emitter.Factory) contructor.newInstance();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            //factory = Emitter.Factory.fromPath(factoryObj.getString("path"));
            if (factory != null) {
                return factory;
            }
        }
        boolean lightMode = factoryObj.optBoolean("light_mode", false);
        switch (JsonStringHelper.getString(factoryObj, "factory_type").toLowerCase(Locale.ENGLISH)) {
            case "blast":
                return BlastParticle.FACTORY;
            case "earth":
                return EarthParticle.FACTORY;
            case "elmo":
                return ElmoParticle.FACTORY;
            case "energy":
                return EnergyParticle.FACTORY;
            case "flame":
                return FlameParticle.FACTORY;
            case "flow":
                return FlowParticle.FACTORY;
            case "leaf":
                return LeafParticle.factory(
                        factoryObj.getInt("first_color"),
                        factoryObj.getInt("second_color")
                );
            case "poison_missile":
                return PoisonParticle.MISSILE;
            case "poison_splash":
                return PoisonParticle.SPLASH;
            case "purple_missile":
                return PurpleParticle.MISSILE;
            case "purple_burst":
                return PurpleParticle.BURST;
                //TODO update server
            case "sacrificial":
                return SacrificialParticle.FACTORY;
            case "shadow_missile":
                return ShadowParticle.MISSILE;
            case "shadow_curse":
                return ShadowParticle.CURSE;
            case "shadow_up":
                return ShadowParticle.UP;
            case "shaft":
                return ShaftParticle.FACTORY;
            case "snow":
                return SnowParticle.FACTORY;
            case "smoke":
                return SmokeParticle.FACTORY;
            case "spark":
                return SparkParticle.FACTORY;
            case "splash":
                return new Splash.SplashFactory(
                        factoryObj.getInt("color"),
                        (float) factoryObj.getDouble("dir"),
                        (float) factoryObj.getDouble("cone")
                );
            case "web":
                return WebParticle.FACTORY;
            case "wind":
                return WindParticle.FACTORY;
            case "wool":
                return WoolParticle.FACTORY;
            case "goo":
                return GooSprite.GooParticle.FACTORY;

            case "speck":
                return Speck.factory(
                        factoryObj.getInt("type"),
                        lightMode
                );

        }
        GLog.n("incorrect factory: " + JsonStringHelper.getString(factoryObj, "factory_type"));
        return null;
    }


    public void parseCell(JSONObject cell) throws JSONException {
        int pos = cell.getInt("position");
        if ((pos < 0) || (pos >= level.length())) {
            GLog.n("incorrect cell position: \"%s\". Ignored.", pos);
            return;
        }
        for (Iterator<String> it = cell.keys(); it.hasNext(); ) {
            String token = it.next();
            switch (token) {
                case "position": {
                    continue;
                }
                case "id": {
                    if(isConnectedToOldServer()) {
                        level.map[pos] = TranslationUtils.translateCell(cell.getInt(token), pos);
                    } else {
                        level.map[pos] = cell.getInt(token);
                    }
                    break;
                }
                case "state": {
                    String state = JsonStringHelper.getString(cell, "state");
                    level.visited[pos] = state.equals("visited");
                    level.mapped[pos] = state.equals("mapped");
                    break;
                }
                default: {
                    GLog.n("Unexpected token \"%s\" in cell. Ignored.", token);
                    break;
                }
            }
        }
    }

    public void parseLevel(JSONObject levelObj) throws JSONException {
        for (Iterator<String> it = levelObj.keys(); it.hasNext(); ) {
            String token = it.next();
            switch (token) {
                case ("cells"): {
                    JSONArray cells = levelObj.getJSONArray(token);
                    for (int i = 0; i < cells.length(); i++) {
                        JSONObject cell = cells.getJSONObject(i);
                        parseCell(cell);
                    }
                    GameScene.updateMap();
                    break;
                }
                case "entrance": {
                    level.entrance = levelObj.getInt("entrance");
                    break;
                }

                case "exit": {
                    level.exit = levelObj.getInt("exit");
                    break;
                }
                case "visible_positions": {
                    JSONArray positions = levelObj.getJSONArray(token);
                    Arrays.fill(Dungeon.level.heroFOV, false);
                    for (int i = 0; i < positions.length(); i++) {
                        int cell = positions.getInt(i);
                        if ((cell < 0) || (cell >= level.length())) {
                            GLog.n("incorrect visible position: \"%s\". Ignored.", cell);
                            continue;
                        }
                        Dungeon.level.heroFOV[cell] = true;
                    }
                    Dungeon.observe();
                    GameScene.setFlag(GameScene.UpdateFlags.AFTER_OBSERVE);
                    break;
                }
                case "states": {
                    JSONArray states = levelObj.getJSONArray("states");
                    for (int i = 0; i < states.length(); i++) {
                        int state = states.getInt(i);
                        level.visited[i] = state == 1;
                        level.mapped[i] = state == 2;
                    }
                    break;
                }
                case "cells_map": {
                    JSONArray map = levelObj.getJSONArray("cells_map");
                    if (isConnectedToOldServer()) {
                        for (int i = 0; i < map.length(); i++) {
                            Dungeon.level.map[i] = TranslationUtils.translateCell(map.getInt(i), i);
                        }
                    } else {
                        for (int i = 0; i < map.length(); i++) {
                            Dungeon.level.map[i] = map.getInt(i);
                        }
                    }
                    break;
                }
                case "custom_tilemaps":
                    JSONObject customTiles = levelObj.getJSONObject("custom_tilemaps");
                    JSONArray tiles = customTiles.getJSONArray("tiles");
                    JSONArray walls = customTiles.getJSONArray("walls");
                    JSONObject tilemap;
                    CustomTilemap customTilemap;

                    for (int i = 0; i < tiles.length(); i++) {
                        tilemap = tiles.getJSONObject(i);
                        String classname = JsonStringHelper.getString(tilemap, "__classname");
                        try {
                            customTilemap = (CustomTilemap) Reflection.newInstance(Reflection.forNameUnhandled(classname));
                            customTilemap.fromJson(tilemap);
                            level.customTiles.add(customTilemap);
                        } catch (Exception e) {
                            Gdx.app.error("ParseThread", "Failed to find class", e);
                        }

                    }
                    for (int i = 0; i < walls.length(); i++) {
                        tilemap = walls.getJSONObject(i);
                        String classname = JsonStringHelper.getString(tilemap, "__classname");
                        try {
                            customTilemap = (CustomTilemap) Reflection.newInstance(Reflection.forNameUnhandled(classname));
                            customTilemap.fromJson(tilemap);
                            level.customWalls.add(customTilemap);
                        } catch (Exception e) {
                            Gdx.app.error("ParseThread", "Failed to find class", e);
                        }

                    }

                    break;
                default: {
                    GLog.n("Unexpected token \"%s\" in level. Ignored.", token);
                    break;
                }
            }
        }
    }

    public void parseLevelParams(JSONObject levelParamsObj) throws JSONException {
        if (hasNotNull(levelParamsObj, "width") || hasNotNull(levelParamsObj, "height")) {
            assert hasNotNull(levelParamsObj, "width") && hasNotNull(levelParamsObj, "height");
            int width = levelParamsObj.getInt("width");
            int height = levelParamsObj.getInt("height");
            if ((width != level.width()) || (height != level.height())) {
                assert Game.scene() instanceof InterlevelScene : "Resizing is allowed only dutring interlevel scene";
                level.setSize(width, height);
            }
        }
        for (Iterator<String> it = levelParamsObj.keys(); it.hasNext(); ) {
            String token = it.next();
            switch (token) {
                case ("width"):
                case ("height"): {
                    //parsed before
                    break;
                }
                case ("tiles_texture"): {
                    if (isConnectedToOldServer()){
                        level.tilesTexture = TranslationUtils.translateTilesTexture(JsonStringHelper.getString(levelParamsObj, token));
                    } else {
                        level.tilesTexture = JsonStringHelper.getString(levelParamsObj, token);
                    }
                    break;
                }
                case ("water_texture"): {
                    if (isConnectedToOldServer()){
                        level.waterTexture = "environment/"+ JsonStringHelper.getString(levelParamsObj, token);
                    }
                    else {
                        level.waterTexture = JsonStringHelper.getString(levelParamsObj, token);
                    }
                    break;
                }
                case "feeling":
                    level.feeling = Level.Feeling.valueOf(JsonStringHelper.getString(levelParamsObj, token));
                    break;
                default: {
                    GLog.n("Unexpected token \"%s\" in level params. Ignored.", token);
                    break;
                }
            }
        }
    }
    //TODO: check this
    public Char parseActorChar(JSONObject actorObj, int ID, Actor actor) throws JSONException {
        final Char chr;
        if (!(actor instanceof Char)) {
            chr = new CustomMob(ID);
            if (actor != null) {
                Actor.remove(actor);
            }
        } else {
            chr = (Char) actor;
        }
        if (!Actor.all().contains(chr)) {
            Actor.add(chr);
        }
        if (chr instanceof CustomMob) {
            if (!level.mobs.contains(chr)) {
                level.mobs.add((Mob) chr);
                GameScene.add((Mob) chr);
            }
        }
        if (actorObj.has("position")) {
            chr.pos = actorObj.getInt("position");
        }
        if (hasNotNull(actorObj,"sprite_name"))
        {
            //deprecated
            CharSprite old_sprite = chr.sprite;
            Class<? extends CharSprite> new_sprite_class;
            if(isConnectedToOldServer()) {
                new_sprite_class = spriteClassFromName(ToPascalCase(JsonStringHelper.getString(actorObj, "sprite_name")), chr != hero);
            } else {
                new_sprite_class = spriteClassFromName(JsonStringHelper.getString(actorObj, "sprite_name"), chr != hero);
            }
            if ((old_sprite == null) || (!old_sprite.getClass().equals(new_sprite_class))) {
                CharSprite sprite = spriteFromClass(new_sprite_class);
                //Do we merge HeroSprite and CustomHeroSprite??

                if (sprite instanceof TieredSprite && actorObj.has("tier")) {
                    ((TieredSprite) sprite).updateTier(actorObj.getInt("tier"));
                }
                if (sprite instanceof ClassSprite && actorObj.has("class")) {
                    HeroClass heroClass = HeroClass.valueOf(JsonStringHelper.getString(actorObj, "class"));
                    ((ClassSprite) sprite).updateHeroClass(heroClass);
                }
                if(!(sprite instanceof HeroSprite)) {
                    GameScene.updateCharSprite(chr, sprite);
                }
            }


            //throw new RuntimeException("Deprecated");
        }

        if (hasNotNull(actorObj,"sprite_asset"))
        {
            CharSprite old_sprite = chr.sprite;
            String spriteAsset = JsonStringHelper.getString(actorObj, "sprite_asset");
            if ((!(old_sprite instanceof CustomCharSprite)) || (!spriteAsset.equals(((CustomCharSprite) old_sprite).getSpriteAsset()))) {
                GameScene.updateCharSprite(chr, new CustomCharSprite(spriteAsset));
            }
        }
        for (Iterator<String> it = actorObj.keys(); it.hasNext(); ) {
            String token = it.next();
            switch (token) {
                case "id":
                    continue;
                case "erase_old": //todo
                    continue;
                case "type": {
                    continue; // it parsed before
                }
                case "position": {
                    chr.pos = actorObj.getInt(token);
                    break;
                }
                case "hp": {
                    chr.HP = actorObj.getInt(token);
                    break;
                }
                case "shield" :{
                    chr.shielding = actorObj.getInt("shield");
                    break;
                }
                case "max_hp": {
                    chr.HT = actorObj.getInt(token);
                    break;
                }
                case "name": {
                    chr.name = JsonStringHelper.getString(actorObj, token);
                    break;
                }
                case "sprite_name": {
                    //already parsed
                    break;
                }
                case "sprite_asset":
                {
                    //already parsed
                    break;
                }
                case "animation_name": {
                    assert false : "animation_name";
                    //todo
                    break;
                }
                case "description": {
                    if (chr instanceof CustomMob) {
                        ((Mob) chr).setDesc(JsonStringHelper.getString(actorObj, token));
                    } else {
                        Gdx.app.error("parseActorChar", actorObj.toString(4));
                    }
                    break;
                }
                case "states": {
                    JSONArray statesArr = actorObj.getJSONArray(token);
                    CharSprite sprite = chr.sprite;
                    if (sprite == null) {
                        break;
                    }
                    Set<CharSprite.State> states = sprite.states();
                    Set<CharSprite.State> newStates = new HashSet<>(3);
                    for (int i = 0; i < statesArr.length(); i++) {
                        try {
                            CharSprite.State state = CharSprite.State.valueOf(JsonStringHelper.optString(statesArr, i).toUpperCase());
                            newStates.add(state);
                            if (states.contains(state)) {
                                continue;
                            }
                            sprite.add(state);
                        } catch (IllegalArgumentException e) {
                            GLog.n("Illegal char state: %s", e.getMessage());
                        }
                    }
                    for (CharSprite.State state : states) {
                        if (!newStates.contains(state)) {
                            sprite.remove(state);
                        }
                    }
                    break;
                }
                case "emo": {
                    CharSprite sprite = chr.sprite;
                    if (sprite == null) {
                        break;
                    }
                    JSONObject emoObj = actorObj.getJSONObject(token);
                    sprite.setEmo(emoObj);
                    break;
                }
                case "class" :
                    break;
                    //already parsed
                case "action_name":
                    break;
                case "tier":
                    if (chr.sprite instanceof HeroCustomSprite){
                        ((HeroCustomSprite) chr.sprite).updateTier(actorObj.getInt("tier"));
                    }
                    if (chr.sprite instanceof HeroSprite){
                        ((HeroSprite) chr.sprite).updateTier(actorObj.getInt("tier"));
                    }
                    break;
                default: {
                    GLog.n("Unexpected token \"%s\" in Actor Char. Ignored.", token);
                    break;
                }
            }
        }
        return chr;
    }

    public void parseActorBlob(JSONObject actorObj, int id, Actor actor) throws JSONException {
        Class blob_class = null;
        if (actor == null) {
            String blob_name;
            if(!isConnectedToOldServer()) {
                blob_name = JsonStringHelper.getString(actorObj, "blob_type");
            } else {
                blob_name = "com.shatteredpixel.shatteredpixeldungeon.actor.blobs." + ToPascalCase(JsonStringHelper.getString(actorObj, "blob_type"));
            }
            try {
                blob_class = ClassReflection.forName(blob_name);
                actor = (Blob) ClassReflection.newInstance(blob_class);
            } catch (Exception e) {
                Gdx.app.error("ParseThread",e.getMessage());
            }
        }
        if (actor == null) return;
        blob_class = actor.getClass();
        if (blob_class == CustomMob.class) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                Gdx.app.error("ParseThread",e.getMessage());
            }
        }
        Blob blob = (Blob) actor;
        blob.clearBlob();
        JSONArray pos_array = actorObj.getJSONArray("positions");
        for (int i = 0; i < pos_array.length(); i += 1) {
            pos_array.get(i);
            GameScene.add(Blob.seed(id, pos_array.getInt(i), 1, blob_class));
        }
    }

    public void parseActorHero(JSONObject actorObj, int id, Actor actor) throws JSONException {
        if ((actor != null) && !(actor instanceof Hero)) {
            Actor.remove(actor);
            Log.e("ParseThread", format("Actor is not hero. Deleted. Id:  %d", id));
        }
        if (hero == null) {
            hero = new Hero();
        }
        if (actor == null) { //hero's actor is not found
            hero.changeID(id);
        }
        actor = hero;
        actor = parseActorChar(actorObj, id, actor);
        Actor.add(actor); // it has check inside, no more checks
    }


    public void parseActors(JSONArray actors) throws JSONException {
        for (int i = 0; i < actors.length(); i++) {
            JSONObject actorObj = actors.getJSONObject(i);
            int ID = actorObj.getInt("id");
            boolean erase_old = false;
            if (actorObj.has("erase_old")) {
                erase_old = actorObj.getBoolean("erase_old");
            }
            if (!actorObj.has("type")) {
                GLog.n("Actor does not have type. Ignored");
                continue;
            }
            Actor actor = (erase_old ? null : Actor.findById(ID));
            String type = JsonStringHelper.getString(actorObj, "type");
            switch (type) {
                case "remove":
                case "removed":
                case "removing": {
                    if (actor instanceof Char) {
                        Char ch = (Char) actor;
                        ch.destroy();
                    } else {
                        Actor.remove(actor);
                    }
                    break;
                }
                case "char":
                case "character": {
                    parseActorChar(actorObj, ID, actor);
                    break;
                }
                case "hero": {
                    parseActorHero(actorObj, ID, actor);
                    break;
                }
                case "blob": {
                    parseActorBlob(actorObj, ID, actor);
                    break;
                }
                default: {
                    GLog.n("can't resolve actor type: \"" + type + "\". ID: " + ID);
                }
            }
        }
    }

    public void parseHero(JSONObject heroObj) throws JSONException {
        for (Iterator<String> it = heroObj.keys(); it.hasNext(); ) {
            String token = it.next();
            switch (token) {
                case "actor_id": {
                    hero.changeID(heroObj.getInt(token));
                    break;
                }
                case "strength": {
                    hero.STR = heroObj.getInt(token);
                    break;
                }
                case "lvl": {
                    hero.lvl = heroObj.getInt(token);
                    break;
                }
                case "exp": {
                    hero.exp = heroObj.getInt(token);
                    break;
                }
                case "class": {
                    String className = JsonStringHelper.getString(heroObj, token);
                    className = className.toUpperCase();
                    hero.heroClass = HeroClass.valueOf(className);
                    hero.sprite = new HeroSprite();
                    break;
                }
                case "ready": {
                    if(Game.scene() instanceof GameScene) {
                        if (heroObj.getBoolean(token)) {
                            hero.ready();
                        } else {
                            hero.busy();
                        }
                    }
                    break;
                }
                case "gold": {
                    hero.gold = heroObj.getInt(token);
                    break;
                }
                case "uuid":{
                        SPDSettings.heroUUID(serverUUID, JsonStringHelper.getString(heroObj, "uuid"));
                        Gdx.app.log("ParseThread", "heroUUID: " + JsonStringHelper.getString(heroObj, "uuid"));
                    break;
                }
                default: {
                    GLog.n("Unexpected token \"%s\" in Hero. Ignored.", token);
                    break;
                }
            }
        }
    }


    public void parseBuffs(JSONArray buffs) {
        for (int i = 0; i < buffs.length(); i++) {
            try {
                JSONObject obj = buffs.getJSONObject(i);
                int id = obj.getInt("id");
                int targetId = obj.optInt("target_id", -1);
                if (targetId == -1) {
                    Buff.detach(id);
                    continue;
                }

                Actor target_actor = Actor.findById(targetId);
                if (!(target_actor instanceof Char)) {
                    Buff.detach(id);
                    continue;
                }
                Char target = (Char) target_actor;

                Buff old_buf = Buff.get(id);
                if ((old_buf instanceof CustomBuff) && (old_buf.target == target)) {
                    ((CustomBuff) old_buf).update(obj);
                    continue;
                }

                CustomBuff buff = new CustomBuff(obj);
                if (!buff.attachTo(target)) {
                    GLog.n("failed to attach buf. Buf id: %d; bug name: %s", buff.buff_id, buff.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    public static String format( String format, Object...args ) {
        return String.format( Locale.ENGLISH, format, args );
    }

    public static String ToPascalCase(String str) {
        str = '_' + str;
        StringBuilder builder = new StringBuilder();
        boolean next_up = false;
        char[] arr = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            if (arr[i] == '_') {
                next_up = true;
            } else {
                if (next_up) {
                    builder.append(Character.toUpperCase(arr[i]));
                    next_up = false;
                } else {
                    builder.append(arr[i]);
                }
            }
        }
        return builder.toString();
    }
    public static boolean isConnectedToOldServer(){
        return isOldServer;
    }

}
