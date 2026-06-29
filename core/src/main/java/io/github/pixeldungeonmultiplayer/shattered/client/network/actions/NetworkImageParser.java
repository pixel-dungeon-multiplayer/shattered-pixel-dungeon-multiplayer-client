package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters.RectParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkImageParser {

    private NetworkImageParser() {
    }

    public static @NotNull Image parse(@NotNull JSONObject object) throws JSONException {
        Image image = new Image(parseTextureSource(object.getJSONObject("source")));
        if (object.has("frame") && !object.isNull("frame")) {
            image.frame(RectParser.parseRectF(object.getJSONObject("frame")));
        } else if (object.has("left")) {
            image.frame(RectParser.parseRectF(object));
        }
        return image;
    }

    private static @NotNull Object parseTextureSource(@NotNull JSONObject source) throws JSONException {
        switch (JsonStringHelper.getString(source, "type")) {
            case "file":
                return JsonStringHelper.getString(source, "path");
            case "solid":
                return TextureCache.createSolid(source.getInt("color"));
            case "gradient":
                return TextureCache.createGradient(parseIntArray(source.getJSONArray("colors")));
            default:
                throw new JSONException("Unknown texture source type: " + JsonStringHelper.getString(source, "type"));
        }
    }

    private static int @NotNull [] parseIntArray(@NotNull JSONArray array) throws JSONException {
        int[] result = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            result[i] = array.getInt(i);
        }
        return result;
    }
}
