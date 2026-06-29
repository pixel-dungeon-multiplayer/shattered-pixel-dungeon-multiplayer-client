package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.PointF;
import io.github.pixeldungeonmultiplayer.common.localizedstring.LocalizedString;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.text.LocalizedStringParser;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowFloatingTextParser implements ActionParser {

    private static final String TYPE_CELL = "cell";
    private static final String TYPE_RAISED_CELL = "raised_cell";
    private static final String TYPE_TARGET = "target";
    private static final String TYPE_TARGET_DESTINATION = "target_destination";

    private final LocalizedStringParser textParser = new LocalizedStringParser();

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONObject anchor = action.getJSONObject("anchor");
        PointF point = parseAnchor(anchor);
        LocalizedString text = textParser.parse(action.get("text"));
        int color = action.getInt("color");
        int icon = action.optInt("icon", FloatingText.NO_ICON);
        boolean left = action.optBoolean("left", false);

        FloatingText.show(point.x, point.y, parseKey(action, anchor), text.resolve(), color, icon, left);
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
                    PointF center = TYPE_TARGET_DESTINATION.equals(type) ? ch.sprite.destinationCenter() : ch.sprite.center();
                    return new PointF(center.x, center.y - ch.sprite.height() / 2f);
                }
                return DungeonTilemap.raisedTileCenterToWorld(ch.pos);
            }
            throw new JSONException("No target char with id " + targetChar);
        }
        throw new JSONException("Invalid floating text anchor type " + type);
    }

    private static int parseKey(JSONObject action, JSONObject anchor) throws JSONException {
        if (action.has("key")) {
            return action.getInt("key");
        }

        String type = anchor.getString("type");
        if (TYPE_CELL.equals(type) || TYPE_RAISED_CELL.equals(type)) {
            return anchor.getInt("cell");
        }
        if (TYPE_TARGET.equals(type) || TYPE_TARGET_DESTINATION.equals(type)) {
            Actor actor = Actor.findById(anchor.getInt("target_char"));
            return actor instanceof Char ? ((Char) actor).pos : -1;
        }
        return -1;
    }
}
