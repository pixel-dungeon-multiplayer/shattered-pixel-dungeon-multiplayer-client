package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import org.json.JSONException;
import org.json.JSONObject;

public class TrapUpdateParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int pos = action.getInt("pos");
        Trap trap = new Trap() {
            @Override
            public void activate() {

            }
        };
        trap.pos = pos;
        trap.shape = action.getInt("shape");
        trap.color = action.getInt("color");
        trap.active = action.getBoolean("active");
        trap.visible = true;

        if (Dungeon.level != null) {
            Dungeon.level.setTrap(trap, pos);
            GameScene.updateMap(pos);
        }
    }
}
