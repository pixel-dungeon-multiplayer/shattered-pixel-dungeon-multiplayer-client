package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.customtilemap;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.FadingTraps;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomTilemapSpecialParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        if (Dungeon.level == null) return;

        boolean isWall = action.getBoolean("isWall");
        int index = action.getInt("index");
        String customAction = action.getString("custom_action");

        ArrayList<CustomTilemap> list = isWall ? Dungeon.level.customWalls : Dungeon.level.customTiles;
        if (index >= 0 && index < list.size()) {
            CustomTilemap tilemap = list.get(index);
            if (tilemap instanceof FadingTraps) {
                if ("fade".equals(customAction)) {
                    ((FadingTraps) tilemap).fade();
                }
            }
        }
    }
}
