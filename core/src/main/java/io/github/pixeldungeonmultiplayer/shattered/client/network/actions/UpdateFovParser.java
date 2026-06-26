package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class UpdateFovParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONArray positions = action.getJSONArray("visible_pos");
        Arrays.fill(Dungeon.level.heroFOV, false);
        for (int i = 0; i < positions.length(); i++) {
            int cell = positions.getInt(i);
            if ((cell < 0) || (cell >= Dungeon.level.length())) {
                GLog.n("incorrect visible position: \"%s\". Ignored.", cell);
                continue;
            }
            Dungeon.level.heroFOV[cell] = true;
        }
        Dungeon.observe();
        GameScene.setFlag(GameScene.UpdateFlags.AFTER_OBSERVE);
    }
}
