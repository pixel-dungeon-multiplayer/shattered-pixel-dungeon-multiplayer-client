package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import io.github.pixeldungeonmultiplayer.shattered.client.network.Client;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.NetworkPacket;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import org.json.JSONException;
import org.json.JSONObject;

public class RedirectServerParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        Client.disconnectWithoutSwitch();
        ShatteredPixelDungeon.switchScene(InterlevelScene.class);
        
        if (action.has("uuid")){
            NetworkPacket.redirectUUID = JsonStringHelper.getString(action, "uuid");
        }
        if(action.has("password")){
            NetworkPacket.password = JsonStringHelper.getString(action, "password");
        }
        Client.connect(JsonStringHelper.getString(action, "host"), action.getInt("port"));
    }
}
