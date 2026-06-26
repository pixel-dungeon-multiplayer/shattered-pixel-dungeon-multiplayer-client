package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters;

import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONException;
import org.json.JSONObject;

public class EmitterStartParser extends BaseEmitterParser implements ActionParser {

	@Override
	public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
		createEmitter(action);
	}
}
