package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.level;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.utils.TranslationUtils;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONException;
import org.json.JSONObject;

import static io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread.isConnectedToOldServer;

public class SetLevelVisualsParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        Level level = Dungeon.level;
        
        if (action.has("tiles_texture")) {
            String tex = JsonStringHelper.getString(action, "tiles_texture");
            level.tilesTexture = isConnectedToOldServer() ? TranslationUtils.translateTilesTexture(tex) : tex;
        }
        
        if (action.has("water_texture")) {
            String tex = JsonStringHelper.getString(action, "water_texture");
            level.waterTexture = isConnectedToOldServer() ? "environment/" + tex : tex;
        }
        
        if (action.has("feeling")) {
            level.feeling = Level.Feeling.valueOf(JsonStringHelper.getString(action, "feeling"));
        }
    }
}
