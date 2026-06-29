package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

class SpellSpriteParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        Actor actor = Actor.findById(action.getInt("target"));
        Char chr = actor instanceof Char ? (Char) actor : null;
        SpellSprite.show(
                chr,
                action.getInt("spell")
        );
    }
}
