package com.shatteredpixel.shatteredpixeldungeon.network.actions.customtilemap;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ActionParser;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomTilemapAddParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        if (Dungeon.level == null) return;

        boolean isWall = action.getBoolean("isWall");
        int index = action.getInt("index");
        JSONObject tilemapObj = action.getJSONObject("tilemap");
        String type = tilemapObj.optString("type", "generic");
        
        try {
            CustomTilemap tilemap = CustomTilemap.createByType(type);
            tilemap.fromJson(tilemapObj);
            
            if (isWall) {
                if (index >= 0 && index <= Dungeon.level.customWalls.size()) {
                    Dungeon.level.customWalls.add(index, tilemap);
                } else {
                    Dungeon.level.customWalls.add(tilemap);
                }
            } else {
                if (index >= 0 && index <= Dungeon.level.customTiles.size()) {
                    Dungeon.level.customTiles.add(index, tilemap);
                } else {
                    Dungeon.level.customTiles.add(tilemap);
                }
            }
            GameScene.add(tilemap, isWall);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add custom tilemap", e);
        }
    }
}
