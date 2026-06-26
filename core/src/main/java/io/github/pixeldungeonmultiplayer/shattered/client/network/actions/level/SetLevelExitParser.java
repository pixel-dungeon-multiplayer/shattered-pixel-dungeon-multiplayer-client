package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.level;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONException;
import org.json.JSONObject;

public class SetLevelExitParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        Dungeon.level.exit = action.getInt("pos");
    }
}
