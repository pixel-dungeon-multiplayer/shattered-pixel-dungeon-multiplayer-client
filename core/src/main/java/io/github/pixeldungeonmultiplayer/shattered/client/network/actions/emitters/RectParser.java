package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters;

import com.watabou.utils.Rect;
import com.watabou.utils.RectF;
import org.json.JSONException;
import org.json.JSONObject;

public class RectParser {

	public static Rect parseRect(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}
		int left = obj.optInt("left", obj.optInt("x", 0));
		int top = obj.optInt("top", obj.optInt("y", 0));
		int right = obj.optInt("right", obj.optInt("width", 0));
		if (obj.has("width")) {
			right += left;
		}
		int bottom = obj.optInt("bottom", obj.optInt("height", 0));
		if (obj.has("height")) {
			bottom += top;
		}
		return new Rect(left, top, right, bottom);
	}

	public static RectF parseRectF(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}
		float left = (float) obj.optDouble("left", obj.optDouble("x", 0.0));
		float top = (float) obj.optDouble("top", obj.optDouble("y", 0.0));
		float right = (float) obj.optDouble("right", obj.optDouble("width", 0.0));
		if (obj.has("width")) {
			right += left;
		}
		float bottom = (float) obj.optDouble("bottom", obj.optDouble("height", 0.0));
		if (obj.has("height")) {
			bottom += top;
		}
		return new RectF(left, top, right, bottom);
	}

	public static Rect optRect(JSONObject obj, String key, Rect fallback) {
		if (obj == null || obj.isNull(key)) {
			return fallback;
		}
		try {
			return parseRect(obj.getJSONObject(key));
		} catch (JSONException e) {
			return fallback;
		}
	}

	public static RectF optRectF(JSONObject obj, String key, RectF fallback) {
		if (obj == null || obj.isNull(key)) {
			return fallback;
		}
		try {
			return parseRectF(obj.getJSONObject(key));
		} catch (JSONException e) {
			return fallback;
		}
	}
}
