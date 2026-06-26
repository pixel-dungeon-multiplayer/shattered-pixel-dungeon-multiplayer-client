package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.json.JSONException;
import org.json.JSONObject;

public class EnchantingVisualParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int targetCharId = action.getInt("target");
        Actor actor = Actor.findById(targetCharId);
        if (!(actor instanceof Char)) {
            GLog.n("Enchanting: Can't find char with id " + targetCharId + ". Ignored");
            return;
        }
        Item item = CustomItem.createItem(action.getJSONObject("item"));
        Enchanting.show((Char) actor, item);
    }
}
