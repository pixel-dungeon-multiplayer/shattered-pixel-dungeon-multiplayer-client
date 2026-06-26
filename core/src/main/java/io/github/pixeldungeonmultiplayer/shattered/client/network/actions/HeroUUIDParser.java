package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import org.json.JSONException;
import org.json.JSONObject;

public class HeroUUIDParser implements ActionParser {

    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        JSONObject payload = DefaultActionParserRegistry.payloadObject(action);
        
        String serverUUID = ParseThread.serverUUID;
        if (payload.has("uuid")) {
            String uuid = JsonStringHelper.getString(payload, "uuid");
            SPDSettings.heroUUID(serverUUID, uuid);
            Gdx.app.log("ParseThread", "heroUUID: " + uuid);
        }
    }
}
