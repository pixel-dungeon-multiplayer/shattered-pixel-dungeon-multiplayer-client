package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class CustomBag extends Bag {
    private int size;

    public Icons bagIcon = Icons.BACKPACK;

    public CustomBag(JSONObject obj) {
        super(obj);
        cursedKnown = true; // todo check it
        size = obj.optInt("size", -1);
        if (size < 0) {
            size = obj.getInt("capacity");
        }
        if (obj.has("owner")) {
            if (!obj.isNull("owner")) {
                owner = (Char) Actor.findById(obj.getInt("owner"));
            }
        }
        if (obj.has("items")) {
            addItemsFromJSONArray(obj.getJSONArray("items"));
        }
        if (obj.has("bag_icon")) {
            try {
                bagIcon = Icons.valueOf(JsonStringHelper.getString(obj, "bag_icon").toUpperCase(Locale.ENGLISH));
            } catch (RuntimeException e) {
                GLog.n("incorrect icon: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void addItemsFromJSONArray(JSONArray arr) {
        for (int i = 0; i < arr.length(); i++) {
            JSONObject itemObj = arr.getJSONObject(i);
            Item item = createItem(itemObj);
            items.add(item);
        }
    }

    @Override
    public int capacity() {
        return size;
    }
}


