package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public abstract class BaseItemActionParser implements ActionParser {
    protected abstract String getMode();

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONArray pathArr = action.getJSONArray("path");
        List<Integer> path = new ArrayList<>(pathArr.length());
        for (int i = 0; i < pathArr.length(); i++) {
            path.add(pathArr.getInt(i));
        }
        
        JSONObject itemObj = action.optJSONObject("item");
        String mode = getMode();
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
}
