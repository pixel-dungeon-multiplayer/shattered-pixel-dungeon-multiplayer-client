package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;

class HeapDropVisualParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        //int from =actionObj.getInt("from");
        int to = action.getInt("to");
        //Item item = CustomItem.createItem(actionObj.getJSONObject("item"));
        //ItemSprite itemSprite = new ItemSprite(item);
        //PixelDungeon.scene().add(itemSprite);
        //itemSprite.drop(from, to);
        Heap heap = level.heaps.get(to);
        if (heap == null) {
            return;
        }
        heap.sprite.drop();
    }
}
