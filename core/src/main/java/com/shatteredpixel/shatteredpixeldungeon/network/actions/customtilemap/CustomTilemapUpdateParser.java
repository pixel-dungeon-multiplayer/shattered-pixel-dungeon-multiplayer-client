package com.shatteredpixel.shatteredpixeldungeon.network.actions.customtilemap;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomTilemapUpdateParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        if (Dungeon.level == null) return;
        
        boolean isWall = action.getBoolean("isWall");
        int index = action.getInt("index");
        JSONObject tilemapObj = action.getJSONObject("tilemap");
        
        ArrayList<CustomTilemap> list = isWall ? Dungeon.level.customWalls : Dungeon.level.customTiles;
        if (index >= 0 && index < list.size()) {
            CustomTilemap oldTilemap = list.get(index);
            if (oldTilemap != null) {
                if (oldTilemap.vis != null) {
                    oldTilemap.vis.killAndErase();
                }
                CustomTilemap newTilemap = CustomTilemap.createByType(tilemapObj.optString("type", "generic"));
                newTilemap.fromJson(tilemapObj);
                list.set(index, newTilemap);
                GameScene.add(newTilemap, isWall);
            }
        }
    }
}
