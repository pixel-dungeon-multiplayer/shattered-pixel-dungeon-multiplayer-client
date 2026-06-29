package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items.inventory;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class InventoryRebuildParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        if (action.has("backpack")) {
            try {
                hero.belongings.backpack = new Belongings.Backpack(action.getJSONObject("backpack"));
            } catch (JSONException e) {
                Log.e("ParseThread", String.format("Can't parse backpack. Stacktrace: %s", e.toString()));
            }
        }
        if (action.has("special_slots")) {
            JSONArray slotsArr;
            try {
                slotsArr = action.getJSONArray("special_slots");
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
}
