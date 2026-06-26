package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.buff;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONException;
import org.json.JSONObject;

public class BuffRemoveParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int id = action.getInt("id");
        Buff.detach(id);
    }
}
