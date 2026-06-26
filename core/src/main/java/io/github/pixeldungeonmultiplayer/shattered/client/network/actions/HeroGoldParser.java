package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

public class HeroGoldParser implements ActionParser {

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONObject payload = DefaultActionParserRegistry.payloadObject(action);
        Hero hero = Dungeon.hero;
        if (hero == null) {
            return;
        }

        if (payload.has("gold")) {
            hero.gold = payload.getInt("gold");
        }
    }
}
