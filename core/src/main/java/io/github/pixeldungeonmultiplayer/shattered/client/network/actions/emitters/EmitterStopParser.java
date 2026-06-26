package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters;

import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import com.watabou.noosa.particles.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

public class EmitterStopParser implements ActionParser {

	@Override
	public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
		Emitter emitter = Emitter.infiniteEmitters.remove(action.getInt("id"));
		if (emitter != null) {
			emitter.on = false;
			emitter.killAndErase();
		}
	}
}
