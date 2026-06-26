package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.level;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import org.json.JSONException;
import org.json.JSONObject;

public class ResizeLevelParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        int width = action.getInt("width");
        int height = action.getInt("height");
        clearCustomTilemaps();
        if ((width != Dungeon.level.width()) || (height != Dungeon.level.height())) {
            Dungeon.level.setSize(width, height);
        }
    }

    private void clearCustomTilemaps() {
        for (CustomTilemap tilemap : Dungeon.level.customTiles) {
            if (tilemap.vis != null) {
                tilemap.vis.killAndErase();
            }
        }
        Dungeon.level.customTiles.clear();
        for (CustomTilemap tilemap : Dungeon.level.customWalls) {
            if (tilemap.vis != null) {
                tilemap.vis.killAndErase();
            }
        }
        Dungeon.level.customWalls.clear();
    }
}
