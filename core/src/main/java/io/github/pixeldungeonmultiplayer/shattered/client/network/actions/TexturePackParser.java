package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import io.github.pixeldungeonmultiplayer.shattered.client.network.utils.JavaUtils;
import io.github.pixeldungeonmultiplayer.shattered.client.network.textures.TextureManager;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

class TexturePackParser implements ActionParser {
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        try {
            String data = action.has("texturepack") ? JsonStringHelper.getString(action, "texturepack") : JsonStringHelper.getString(action, "payload");
            TextureManager.INSTANCE.loadTexturePack(JavaUtils.InputStreamFromBase64(data));
        } catch (IOException err) {
            ShatteredPixelDungeon.scene().add(new WndError("Malformed texture pack"));
        }
    }
}
