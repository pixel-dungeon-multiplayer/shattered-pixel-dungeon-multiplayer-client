package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

class SpriteActionParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int actorID = action.getInt("actor_id");
        Actor actor = Actor.findById(actorID);
        if (actor == null) {
            GLog.h("solve actor");
            return;
        }
        CharSprite sprite = ((Char) actor).sprite;
        if (sprite == null) {
            GLog.h("actor " + actorID + "has null sprite");
            return;
        }

        sprite.parseAction(action);
    }
}
