package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.PointF;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LightningVisualParser implements ActionParser {

    private static final String TYPE_CELL = "cell";
    private static final String TYPE_RAISED_CELL = "raised_cell";
    private static final String TYPE_TARGET = "target";
    private static final String TYPE_TARGET_DESTINATION = "target_destination";
    private static final String TYPE_TARGET_POINT = "target_point";

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        float duration = (float) action.optDouble("duration", 0.3f);

        JSONArray arcsArray = action.getJSONArray("arcs");
        ArrayList<Lightning.Arc> arcs = new ArrayList<>();
        for (int i = 0; i < arcsArray.length(); i++) {
            JSONObject arc = arcsArray.getJSONObject(i);
            arcs.add(new Lightning.Arc(parseAnchor(arc.getJSONObject("from")), parseAnchor(arc.getJSONObject("to"))));
        }

        if (!arcs.isEmpty()) {
            GameScene.effect(new Lightning(arcs, null, duration));
        }
    }

    private static PointF parseAnchor(JSONObject anchor) throws JSONException {
        String type = anchor.getString("type");
        if (TYPE_CELL.equals(type)) {
            return DungeonTilemap.tileCenterToWorld(anchor.getInt("cell"));
        }
        if (TYPE_RAISED_CELL.equals(type)) {
            return DungeonTilemap.raisedTileCenterToWorld(anchor.getInt("cell"));
        }
        if (TYPE_TARGET.equals(type) || TYPE_TARGET_DESTINATION.equals(type) || TYPE_TARGET_POINT.equals(type)) {
            int targetChar = anchor.getInt("target_char");
            Actor actor = Actor.findById(targetChar);
            if (actor instanceof Char) {
                Char ch = (Char) actor;
                if (ch.sprite != null) {
                    if (TYPE_TARGET_DESTINATION.equals(type)) {
                        return ch.sprite.destinationCenter();
                    }
                    if (TYPE_TARGET_POINT.equals(type)) {
                        float xFactor = (float) anchor.getDouble("x_factor");
                        float yFactor = (float) anchor.getDouble("y_factor");
                        float shiftX = (float) anchor.optDouble("shift_x", 0f);
                        float shiftY = (float) anchor.optDouble("shift_y", 0f);
                        return new PointF(
                                ch.sprite.x + ch.sprite.width() * xFactor + shiftX * ch.sprite.scale.x,
                                ch.sprite.y + ch.sprite.height() * yFactor + shiftY * ch.sprite.scale.y);
                    }
                    return ch.sprite.center();
                }
                return DungeonTilemap.raisedTileCenterToWorld(ch.pos);
            }
            throw new JSONException("No target char with id " + targetChar);
        }
        throw new JSONException("Invalid lightning anchor type " + type);
    }
}
