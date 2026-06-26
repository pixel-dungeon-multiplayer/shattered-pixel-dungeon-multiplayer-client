package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.level;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateCellsParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONArray positions = action.getJSONArray("positions");
        JSONArray tiles = action.optJSONArray("tiles");
        JSONArray states = action.optJSONArray("states");
        
        Level level = Dungeon.level;
        
        for (int i = 0; i < positions.length(); i++) {
            int pos = positions.getInt(i);
            
            if (tiles != null) {
                level.map[pos] = tiles.getInt(i);
            }
            
            if (states != null) {
                int state = states.getInt(i);
                level.visited[pos] = (state == 1);
                level.mapped[pos] = (state == 2);
            }
        }
        
        GameScene.updateMap();
    }
}
