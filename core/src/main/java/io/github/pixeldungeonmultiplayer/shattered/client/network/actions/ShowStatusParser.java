package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

class ShowStatusParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        float x = (float) action.getDouble("x");
        float y = (float) action.getDouble("y");
        Integer key = action.has("key") ? action.getInt("key") : null;
        String text = JsonStringHelper.getString(action, "text");
        int color = action.getInt("color");
        boolean ignore_position = action.optBoolean("ignore_position", true);
        if ((key != null) && ignore_position) {
            Char ch = Actor.findChar(key);
            if ((ch != null) && (ch.sprite != null)) {
                ch.sprite.showStatus(color, text);
                return;
            }
        }
        if (key == null) {
            FloatingText.show(x, y, text, color);
        } else {
            FloatingText.show(x, y, key, text, color);
        }
    }
}
