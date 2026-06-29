package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

class DiscoverTileParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        GameScene.discoverTile(action.getInt("pos"), action.getInt("old_tile"));
    }
}
