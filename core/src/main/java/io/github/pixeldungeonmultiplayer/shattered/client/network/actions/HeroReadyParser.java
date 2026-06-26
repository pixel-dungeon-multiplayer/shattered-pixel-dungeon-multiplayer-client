package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Game;
import org.json.JSONException;
import org.json.JSONObject;

public class HeroReadyParser implements ActionParser {

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONObject payload = DefaultActionParserRegistry.payloadObject(action);
        Hero hero = Dungeon.hero;
        if (hero == null) {
            return;
        }

        if (payload.has("ready")) {
            boolean ready = payload.getBoolean("ready");
            if (Game.scene() instanceof GameScene) {
                if (ready) {
                    hero.ready();
                } else {
                    hero.busy();
                }
            }
        }
    }
}
