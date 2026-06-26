package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.effects;

import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.PointF;
import org.json.JSONException;
import org.json.JSONObject;

public class FlareVisualParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        PointF position;
        if (action.has("pos")) {
            position = DungeonTilemap.tileCenterToWorld(action.getInt("pos"));
        } else {
            position = new PointF(
                    (float) action.getDouble("position_x"),
                    (float) action.getDouble("position_y")
            );
        }

        Flare flare = new Flare(action.getInt("rays"), (float) action.getDouble("radius"));
        flare.angle = (float) action.optDouble("angle", 45);
        flare.angularSpeed = (float) action.optDouble("angular_speed", 180);
        flare.color(action.getInt("color"), action.optBoolean("light_moode", true));
        GameScene.showFlare(flare, position, (float) action.getDouble("duration"));
    }
}
