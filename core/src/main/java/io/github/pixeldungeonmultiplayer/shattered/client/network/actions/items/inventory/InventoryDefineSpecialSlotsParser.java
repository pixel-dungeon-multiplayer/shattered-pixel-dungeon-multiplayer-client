package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items.inventory;

import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InventoryDefineSpecialSlotsParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        //todo implement this
        JSONArray slotsArr = action.getJSONArray("slots");
        Log.w("ParseThread", "inventory_define_special_slots received, but dynamic slots are not implemented on client yet. Slots count: " + slotsArr.length());
    }
}
