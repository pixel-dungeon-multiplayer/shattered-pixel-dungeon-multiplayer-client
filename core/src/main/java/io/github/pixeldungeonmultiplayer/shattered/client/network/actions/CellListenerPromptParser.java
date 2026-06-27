package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import io.github.pixeldungeonmultiplayer.common.localizedstring.LocalizedString;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import org.json.JSONObject;

public class CellListenerPromptParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) {
        GameScene.defaultCellListener.setCustomPrompt(JsonStringHelper.optString(action, "prompt", null));
    }
}
