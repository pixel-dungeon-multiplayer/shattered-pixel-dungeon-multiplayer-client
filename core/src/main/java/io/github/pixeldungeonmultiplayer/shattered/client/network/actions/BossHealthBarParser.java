package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONObject;

class BossHealthBarParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) {
        BossHealthBar.parseAction(action);
    }
}
