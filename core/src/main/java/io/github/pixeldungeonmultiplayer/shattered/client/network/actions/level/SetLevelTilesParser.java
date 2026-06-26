package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.level;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.utils.TranslationUtils;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread.isConnectedToOldServer;

public class SetLevelTilesParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONArray map = action.getJSONArray("tiles");
        if (isConnectedToOldServer()) {
            for (int i = 0; i < map.length(); i++) {
                Dungeon.level.map[i] = TranslationUtils.translateCell(map.getInt(i), i);
            }
        } else {
            for (int i = 0; i < map.length(); i++) {
                Dungeon.level.map[i] = map.getInt(i);
            }
        }
    }
}
