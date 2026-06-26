package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONException;
import org.json.JSONObject;

public class TrapRemoveParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int pos = action.getInt("pos");
        if (Dungeon.level != null) {
            Dungeon.level.traps.remove(pos);
            com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene.updateMap(pos);
        }
    }
}
