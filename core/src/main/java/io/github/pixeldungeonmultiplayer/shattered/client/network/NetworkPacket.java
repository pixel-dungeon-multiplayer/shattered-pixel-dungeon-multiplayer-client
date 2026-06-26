package io.github.pixeldungeonmultiplayer.shattered.client.network;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Locale;

public class NetworkPacket {


    enum CellState {
        VISITED, UNVISITED, MAPPED;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.getDefault());
        }
    }

    public static final String CELLS = "cells";
    public static final String MAP = "map";
    public static final String ACTORS = "actors";
    public static final String CHAT = "chat";

    public AtomicReference<JSONObject> dataRef = new AtomicReference<>(new JSONObject());

    public void clearData() {
        synchronized (dataRef) {
            dataRef.set(new JSONObject());
        }
    }
    public static String password = null;
    public static String redirectUUID = null;

    public void packAndAddHeroClass(String heroClass) {
        synchronized (dataRef) {
            try {
                if (ParseThread.isConnectedToOldServer()) {
                    dataRef.get().put("hero_class", heroClass);
                } else {
                    dataRef.get().put(Protocol.FIELD_PACKET_TYPE, Protocol.PACKET_JOIN);
                    dataRef.get().put(Protocol.FIELD_PROTOCOL, Protocol.NAME);
                    dataRef.get().put(Protocol.FIELD_VERSION, ParseThread.negotiatedProtocolVersion);
                    dataRef.get().put("hero_class", heroClass);
                }
                dataRef.get().put("uuid", redirectUUID != null ? redirectUUID : SPDSettings.heroUUID(ParseThread.serverUUID));
                if (password!= null) {
                    dataRef.get().put("password", password);
                }
                redirectUUID = null;
                Gdx.app.log("ServerUUID", ParseThread.serverUUID);
            } catch (Exception ignored) {
            }
        }
    }

    public void packAndAddCellListenerCell(Integer cell) {
        synchronized (dataRef) {
            try {
                if (cell == null) {
                    dataRef.get().put("cell_listener", -1);
                } else {
                    dataRef.get().put("cell_listener", cell);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void packAndAddUsedAction(CustomItem item, String action, Hero hero) {
        assert !action.equals("");

        JSONObject action_obj = new JSONObject();
        action_obj.put("action_name", action);
        List<Integer> slot = hero.belongings.pathOfItem(item);
        if (slot == null) {
            throw new IllegalArgumentException("slot is null");
        }

        assert !slot.isEmpty() : "slot is empty";

        action_obj.put("slot", new JSONArray(slot));

        synchronized (dataRef) {
            dataRef.get().put("action", action_obj);
        }
    }

    public void packAndAddWindowsResult(int id, int pressedButton, JSONObject args) {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("button", pressedButton);
        obj.put("result", args);
        synchronized (dataRef) {
            dataRef.get().put("window", obj);
        }
    }
    public void packAndAddTollbarAction(String action) {
        JSONObject obj = new JSONObject();
        obj.put("action_name", action);
        synchronized (dataRef) {
            dataRef.get().put("toolbar_action", obj);
        }
    }

    public void packAndAddExamineAction(int cell) {
        JSONObject obj = new JSONObject();
        obj.put("action_name", "examine");
        obj.put("cell", cell);
        synchronized (dataRef) {
            dataRef.get().put("toolbar_action", obj);
        }
    }

    public void packAndAddTalentUpgrade(Talent talent) {
        synchronized (dataRef) {
            dataRef.get().put("talent_upgrade", talent.id());
        }
    }

    public void packAndAddChatMessage(String text) {
        synchronized (dataRef) {
            dataRef.get().put(CHAT, new JSONObject().put("message", text));
        }
    }

}
