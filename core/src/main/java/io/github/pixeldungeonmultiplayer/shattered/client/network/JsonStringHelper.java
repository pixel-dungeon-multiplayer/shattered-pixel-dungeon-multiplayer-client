package io.github.pixeldungeonmultiplayer.shattered.client.network;

import io.github.pixeldungeonmultiplayer.common.localizedstring.LocalizedString;
import io.github.pixeldungeonmultiplayer.shattered.client.network.text.LocalizedStringParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonStringHelper {

    private static final LocalizedStringParser TEXT_PARSER = new LocalizedStringParser();

    private JsonStringHelper() {
        // Utility class
    }

    public static String getString(JSONObject obj, String key) throws JSONException {
        LocalizedString text = getLocalizedString(obj, key);
        if (text == null) {
            throw new JSONException("Value is null: " + key);
        }
        return text.resolve();
    }

    public static String optString(JSONObject obj, String key, String fallback) {
        LocalizedString text = optLocalizedString(obj, key, null);
        return text == null ? fallback : text.resolve();
    }

    public static String optString(JSONObject obj, String key) {
        return optString(obj, key, "");
    }

    public static String getString(JSONArray arr, int index) throws JSONException {
        LocalizedString text = getLocalizedString(arr, index);
        if (text == null) {
            throw new JSONException("Value is null: " + index);
        }
        return text.resolve();
    }

    public static String optString(JSONArray arr, int index, String fallback) {
        LocalizedString text = optLocalizedString(arr, index, null);
        return text == null ? fallback : text.resolve();
    }

    public static LocalizedString getLocalizedString(JSONObject obj, String key) throws JSONException {
        return TEXT_PARSER.parse(obj.get(key));
    }

    public static LocalizedString optLocalizedString(JSONObject obj, String key, LocalizedString fallback) {
        if (obj == null || obj.isNull(key)) {
            return fallback;
        }
        try {
            return TEXT_PARSER.parse(obj.get(key));
        } catch (JSONException e) {
            return fallback;
        }
    }

    public static String optString(JSONArray arr, int index) {
        return optString(arr, index, "");
    }

    public static LocalizedString getLocalizedString(JSONArray arr, int index) throws JSONException {
        return TEXT_PARSER.parse(arr.get(index));
    }

    public static LocalizedString optLocalizedString(JSONArray arr, int index, LocalizedString fallback) {
        if (arr == null || index < 0 || index >= arr.length() || arr.isNull(index)) {
            return fallback;
        }
        try {
            return TEXT_PARSER.parse(arr.get(index));
        } catch (JSONException e) {
            return fallback;
        }
    }
}
