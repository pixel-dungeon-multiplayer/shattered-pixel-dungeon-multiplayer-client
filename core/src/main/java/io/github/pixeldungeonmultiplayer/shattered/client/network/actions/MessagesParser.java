package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import com.watabou.noosa.Game;
import com.watabou.noosa.Scene;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessagesParser implements ActionParser {

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONArray messages = action.has("messages")
                ? action.getJSONArray("messages")
                : DefaultActionParserRegistry.payloadArray(action);

        for (int i = 0; i < messages.length(); i++) {
            parseMessage(messages.getJSONObject(i));
        }
    }

    private void parseMessage(JSONObject messageObj) {
        Scene scene = Game.scene();
        if (!(scene instanceof GameScene)) {
            return;
        }

        GameLog log = ((GameScene) scene).getGameLog();
        try {
            String resolved = JsonStringHelper.getString(messageObj, "text");
            if (messageObj.has("color")) {
                log.WriteMessage(resolved, messageObj.getInt("color"));
            } else {
                log.WriteMessageAutoColor(resolved);
            }
        } catch (JSONException e) {
            Log.w("MessagesParser", "Incorrect message");
        }
    }
}
