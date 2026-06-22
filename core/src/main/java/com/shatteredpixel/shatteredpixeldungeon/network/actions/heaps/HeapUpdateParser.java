package com.shatteredpixel.shatteredpixeldungeon.network.actions.heaps;

import com.nikita22007.multiplayer.utils.text.LocalizedString;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import com.watabou.utils.DeviceCompat;
import org.json.JSONException;
import org.json.JSONObject;

public class HeapUpdateParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        if (Dungeon.level == null) {
            if (DeviceCompat.isDebug()) {
                throw new IllegalStateException("Dungeon level is null");
            }
            Log.e("Can't parse heap : Dungeon level is null");
        }
        int pos = action.getInt("pos");
        Item item = CustomItem.createItem(action.getJSONObject("peek"));
        LocalizedString title = JsonStringHelper.getLocalizedString( action, "title");
        LocalizedString info = JsonStringHelper.getLocalizedString( action, "info");
        boolean seen = action.getBoolean("seen");
        boolean hidden = action.getBoolean("hidden");
        Heap heap = Dungeon.level.heaps.get(pos, null);
        if (heap == null) {
            heap = new Heap(pos);
            heap.update(item, title, info, seen, hidden);
            Dungeon.level.heaps.put(pos, heap); //logical
            GameScene.add(heap); //visual
        } else  {
            heap.update(item, title, info, seen, hidden);
        }
    }
}
