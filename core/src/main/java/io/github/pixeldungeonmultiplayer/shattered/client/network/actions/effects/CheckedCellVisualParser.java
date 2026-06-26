package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CheckedCell;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckedCellVisualParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        if (Dungeon.level.heroFOV[action.getInt("pos")]) {
            GameScene.effect(new CheckedCell(action.getInt("pos")));
        }
    }
}
