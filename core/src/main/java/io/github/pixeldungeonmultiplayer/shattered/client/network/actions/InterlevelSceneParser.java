package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.watabou.noosa.Game;
import org.json.JSONException;
import org.json.JSONObject;

class InterlevelSceneParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONObject ilsObj = DefaultActionParserRegistry.payloadObject(action);
        if (ilsObj.has("state")) {
            InterlevelScene.phase = InterlevelScene.Phase.valueOf(JsonStringHelper.getString(ilsObj, "state").toUpperCase());
        }
        if (ilsObj.has("type")) {
            String modeName = JsonStringHelper.getString(ilsObj, "type").toUpperCase();
            if (modeName.equals("CUSTOM")) {
                modeName = "NONE";
            }
            InterlevelScene.mode = InterlevelScene.Mode.valueOf(modeName);
        }
        InterlevelScene.reset_level = ilsObj.optBoolean("reset_level");
        if (ilsObj.has("custom_message")) {
            InterlevelScene.customMessage = JsonStringHelper.getString(ilsObj, "custom_message");
        }
        if (!(Game.scene() instanceof InterlevelScene)) {
            if (!((Game.scene() instanceof GameScene) && (InterlevelScene.phase == InterlevelScene.Phase.FADE_OUT))) {
                Game.switchScene(InterlevelScene.class);
            }
        }
    }
}
