package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.watabou.noosa.Group;

import org.json.JSONException;
import org.json.JSONObject;

public class MagicMissileVisualParser implements ActionParser {

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int from = action.getInt("from");
        int to = action.getInt("to");
        Group group = GameScene.mobSpriteGroup();
        Object type = action.get("type");
        if (type instanceof String) {
            MagicMissile.show((String) type, from, to, group);
        } else {
            MagicMissile.show(action.getInt("type"), from, to, group);
        }
    }
}
