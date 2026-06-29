package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

class SurpriseVisualParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        Surprise.hit(action.getInt("pos"), action.getInt("angle"));
    }
}
