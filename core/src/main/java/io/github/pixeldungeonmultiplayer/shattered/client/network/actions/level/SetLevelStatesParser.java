package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.level;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetLevelStatesParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONArray states = action.getJSONArray("states");
        Level level = Dungeon.level;
        for (int i = 0; i < states.length(); i++) {
            int state = states.getInt(i);
            level.visited[i] = (state == 1);
            level.mapped[i] = (state == 2);
        }
    }
}
