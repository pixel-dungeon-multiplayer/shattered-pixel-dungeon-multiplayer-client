package io.github.pixeldungeonmultiplayer.shattered.client.network;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CustomMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParserRegistry;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.DefaultActionParserRegistry;
import com.shatteredpixel.shatteredpixeldungeon.plants.CustomPlant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PointF;
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

import static io.github.pixeldungeonmultiplayer.shattered.client.network.utils.JavaUtils.hasNotNull;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;
import static io.github.pixeldungeonmultiplayer.shattered.client.network.Client.disconnect;
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
            if ("sprite_action".equals(type) && false) {
                spriteActions.add(actionObj);
                continue;
            }
            parseAction(type, actionObj);
        }
        for (JSONObject actionObj : spriteActions) {
            parseAction(JsonStringHelper.optString(actionObj, "action_name", actionObj.optString("action_type")), actionObj);
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
            Log.e("v2parser", actionObj.toString(4));
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
