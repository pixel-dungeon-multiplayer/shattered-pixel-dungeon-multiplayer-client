package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.watabou.noosa.Group;

import org.json.JSONException;
import org.json.JSONObject;

public class MagicMissileVisualParser implements ActionParser {

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int from = action.getInt("from");
        int to = action.getInt("to");
        Char actor = Actor.findChar(from);
        Group group = null;
        if ((actor != null) && (actor.sprite != null)) {
            group = actor.sprite.parent;
        }
        Object type = action.get("type");
        if (type instanceof String) {
            MagicMissile.show((String) type, from, to, group);
        } else {
            MagicMissile.show(action.getInt("type"), from, to, group);
        }
    }
}
