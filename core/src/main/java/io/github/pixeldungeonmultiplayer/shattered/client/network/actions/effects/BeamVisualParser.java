package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Image;
import com.watabou.utils.PointF;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.NetworkImageParser;
import org.json.JSONException;
import org.json.JSONObject;

public class BeamVisualParser implements ActionParser {

    private static final String TYPE_CELL = "cell";
    private static final String TYPE_RAISED_CELL = "raised_cell";
    private static final String TYPE_TARGET = "target";
    private static final String TYPE_TARGET_DESTINATION = "target_destination";

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        Image image = NetworkImageParser.parse(action.getJSONObject("image"));
        applyColor(image, action.optJSONObject("color"));

        PointF from = parseAnchor(action.getJSONObject("from"));
        PointF to = parseAnchor(action.getJSONObject("to"));
        float duration = (float) action.getDouble("duration");

        GameScene.effect(new Beam(image, from, to, duration, false));
    }

    private static PointF parseAnchor(JSONObject anchor) throws JSONException {
        String type = anchor.getString("type");
        if (TYPE_CELL.equals(type)) {
            return DungeonTilemap.tileCenterToWorld(anchor.getInt("cell"));
        }
        if (TYPE_RAISED_CELL.equals(type)) {
            return DungeonTilemap.raisedTileCenterToWorld(anchor.getInt("cell"));
        }
        if (TYPE_TARGET.equals(type) || TYPE_TARGET_DESTINATION.equals(type)) {
            int targetChar = anchor.getInt("target_char");
            Actor actor = Actor.findById(targetChar);
            if (actor instanceof Char) {
                Char ch = (Char) actor;
                if (ch.sprite != null) {
                    return TYPE_TARGET_DESTINATION.equals(type) ? ch.sprite.destinationCenter() : ch.sprite.center();
                }
                return DungeonTilemap.raisedTileCenterToWorld(ch.pos);
            }
            throw new JSONException("No target char with id " + targetChar);
        }
        throw new JSONException("Invalid beam anchor type " + type);
    }

    private static void applyColor(Image image, JSONObject color) throws JSONException {
        if (color == null) {
            return;
        }
        image.rm = (float) color.optDouble("rm", image.rm);
        image.gm = (float) color.optDouble("gm", image.gm);
        image.bm = (float) color.optDouble("bm", image.bm);
        image.am = (float) color.optDouble("am", image.am);
        image.ra = (float) color.optDouble("ra", image.ra);
        image.ga = (float) color.optDouble("ga", image.ga);
        image.ba = (float) color.optDouble("ba", image.ba);
        image.aa = (float) color.optDouble("aa", image.aa);
    }
}
