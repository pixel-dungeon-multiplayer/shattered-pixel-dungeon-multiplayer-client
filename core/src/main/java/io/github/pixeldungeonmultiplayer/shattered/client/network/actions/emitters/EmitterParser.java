package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters;

import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParticleFactoryDeserializer;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.particles.Emitter;
import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

public class EmitterParser {

	@Contract("null, _ -> false; _, null->false")
	public static boolean configure(Emitter emitter, JSONObject json) throws JSONException {
		if (json == null || emitter == null) {
			return false;
		}

		if (json.has("factory")) {
			Emitter.Factory factory = ParticleFactoryDeserializer.deserialize(json.getJSONObject("factory"));
			if (factory == null) {
				GLog.n("incorrect emitter factory: " + json.getJSONObject("factory").optString("factory_type"));
				return false;
			}

			float interval = (float) json.optDouble("interval", 0);
			int quantity = json.optInt("quantity", 0);

			emitter.start(factory, interval, quantity);
		}

		if (json.has("anchor")) {
			if (!EmitterAnchorParser.apply(emitter, json.getJSONObject("anchor"))) {
				GLog.n("incorrect emitter anchor");
				return false;
			}
		}

		if (json.has("fill_target")) {
			emitter.fillTarget = json.optBoolean("fill_target", emitter.fillTarget);
		}
		if (emitter instanceof BlobEmitter) {
			if (json.has("bound")) {
				((BlobEmitter) emitter).bound = RectParser.parseRectF(json.getJSONObject("bound"));
			}
		}
		return true;
	}
}
