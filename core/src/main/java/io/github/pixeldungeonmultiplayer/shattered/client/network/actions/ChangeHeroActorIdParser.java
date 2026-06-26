package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

public class ChangeHeroActorIdParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        if (!action.has("actor_id")) {
            return;
        }
        int actorId = action.getInt("actor_id");
        Hero hero = Dungeon.hero;
        if (hero == null) {
            Dungeon.hero = new Hero();
            hero = Dungeon.hero;
            hero.changeID(actorId);
            Actor.add(hero);
        } else {
            Actor.remove(hero);
            hero.changeID(actorId);
            Actor.add(hero);
        }
    }
}
