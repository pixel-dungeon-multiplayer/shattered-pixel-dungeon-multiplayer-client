package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.charsprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class CharSpriteStateParser implements ActionParser {

    private final boolean remove;

    public CharSpriteStateParser(boolean remove) {
        this.remove = remove;
    }

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        Actor actor = Actor.findById(action.getInt("actor_id"));
        if (!(actor instanceof Char)) {
            return;
        }

        CharSprite sprite = ((Char) actor).sprite;
        if (sprite == null) {
            return;
        }

        try {
            CharSprite.State state = CharSprite.State.valueOf(JsonStringHelper.getString(action, "state").toUpperCase(Locale.ROOT));
            if (remove) {
                sprite.remove(state);
            } else {
                sprite.add(state);
            }
        } catch (IllegalArgumentException e) {
            GLog.n("Illegal char state: %s", e.getMessage());
        }
    }
}
