package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.PointF;
import org.json.JSONException;
import org.json.JSONObject;

public class MissileSpriteVisualParser implements ActionParser {

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONObject fromObj = action.getJSONObject("from");
        JSONObject toObj = action.getJSONObject("to");

        PointF from = getAnchorPoint(fromObj);
        PointF to = getAnchorPoint(toObj);

        float speed = (float) action.getDouble("speed");
        float angularSpeed = (float) action.getDouble("angular_speed");
        float angle = (float) action.getDouble("angle");
        boolean flipHorizontal = action.getBoolean("flip_horizontal");

        Item item = null;
        if (!action.isNull("item")) {
            item = CustomItem.createItem(action.getJSONObject("item"));
        }

        MissileSprite sprite = (MissileSprite) GameScene.recycleSprite(MissileSprite.class);
        if (sprite != null) {
            sprite.reset(from, to, speed, angularSpeed, angle, flipHorizontal, item);
        }
    }

    private PointF getAnchorPoint(JSONObject anchorObj) throws JSONException {
        String type = anchorObj.getString("type");
        if ("cell".equals(type)) {
            return DungeonTilemap.raisedTileCenterToWorld(anchorObj.getInt("cell"));
        } else if ("char".equals(type)) {
            int charId = anchorObj.getInt("char_id");
            Actor actor = Actor.findById(charId);
            if (actor instanceof Char) {
                Char ch = (Char) actor;
                if (ch.sprite != null) {
                    return ch.sprite.center();
                } else {
                    return DungeonTilemap.raisedTileCenterToWorld(ch.pos);
                }
            }
            throw new JSONException("No char with id " + charId);
        }
        throw new JSONException("Invalid anchor type " + type);
    }
}
