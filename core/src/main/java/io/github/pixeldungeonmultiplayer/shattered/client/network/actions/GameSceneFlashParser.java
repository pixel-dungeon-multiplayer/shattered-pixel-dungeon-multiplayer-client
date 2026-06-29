package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

class GameSceneFlashParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        GameScene.flash(action.getInt("color"), action.getBoolean("light"));
    }
}
