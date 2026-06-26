package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items.inventory;

import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONException;
import org.json.JSONObject;

public class InventoryDefineSpecialSlotsParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        parseThread.parseInventoryDefineSpecialSlots(action.getJSONArray("slots"));
    }
}
