package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import org.json.JSONException;
import org.json.JSONObject;

public class AttackIndicatorTargetParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        AttackIndicator.setCandidates(action.getInt("target"));
    }
}
