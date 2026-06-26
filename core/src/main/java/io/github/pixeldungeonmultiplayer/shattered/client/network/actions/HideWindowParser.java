package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import org.json.JSONException;
import org.json.JSONObject;

public class HideWindowParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        Window window = Window.getWindow(action.getInt("id"));
        if (window != null) window.hide();
    }
}
