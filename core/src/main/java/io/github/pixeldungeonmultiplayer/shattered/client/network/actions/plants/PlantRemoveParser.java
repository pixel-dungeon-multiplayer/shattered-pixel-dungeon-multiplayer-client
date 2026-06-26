package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONException;
import org.json.JSONObject;

public class PlantRemoveParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int pos = action.getInt("pos");
        if (Dungeon.level != null) {
            Dungeon.level.plants.remove(pos);
            com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene.updateMap(pos);
        }
    }
}
