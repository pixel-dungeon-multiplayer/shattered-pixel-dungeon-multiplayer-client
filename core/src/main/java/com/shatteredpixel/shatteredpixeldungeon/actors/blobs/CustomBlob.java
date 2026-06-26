package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import io.github.pixeldungeonmultiplayer.shattered.client.network.utils.JavaUtils;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public final class CustomBlob extends Blob {
    @Nullable
    private String tileDesc = null;
    @Nullable
    private JSONObject emitterInfo = null;

    public CustomBlob(int id) {
        super();
        setId(id);
    }

    public void update(JSONObject actorObj) {
        tileDesc = JsonStringHelper.optString(actorObj, "tile_desc");
        alwaysVisible = actorObj.getBoolean("always_visible");
        emitterInfo = actorObj.optJSONObject("emitter");
        cur = new int[Dungeon.level.length()];
        int[] positions = JavaUtils.JSONArrayToIntArray(actorObj.getJSONArray("positions"));
        for (int position : positions) {
            cur[position] = 1;
        }
        volume = positions.length;
        updateEmitter();
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        updateEmitter();
    }

    private void updateEmitter() {
        if (emitter == null) {
            return;
        }
        if (emitterInfo != null) {
            emitter.update(emitterInfo);
        }
    }

    @Contract(pure = true)
    @Nullable
    @Override
    public String tileDesc() {
        return tileDesc;
    }

    @Contract(pure = true)
    @Override
    public boolean act() {
        return true;
    }
}
