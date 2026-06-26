package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;
import org.json.JSONException;
import org.json.JSONObject;

public class EmitterAnchorParser {

	private static final String TYPE_WORLD = "world";
	private static final String TYPE_CELL = "cell";
	private static final String TYPE_TARGET = "target";

	public static boolean apply(Emitter emitter, JSONObject anchor) throws JSONException {
		String type = anchor.getString("type");
		float x = (float) anchor.optDouble("x", 0);
		float y = (float) anchor.optDouble("y", 0);
		float width = (float) anchor.optDouble("width", 0);
		float height = (float) anchor.optDouble("height", 0);
		float shiftX = (float) anchor.optDouble("shift_x", 0);
		float shiftY = (float) anchor.optDouble("shift_y", 0);

		if (TYPE_TARGET.equals(type)) {
			Actor actor = Actor.findById(anchor.getInt("target_char"));
			if (!(actor instanceof Char)) {
				return false;
			}
			Char target = (Char) actor;
			if (target.sprite == null) {
				return false;
			}
			emitter.pos(target.sprite, x, y, width, height);
			emitter.fillTarget = anchor.optBoolean("fill_target", true);
			emitter.shift(shiftX, shiftY);
			return true;
		}

		if (TYPE_CELL.equals(type)) {
			int cell = anchor.getInt("cell");
			PointF position = DungeonTilemap.tileToWorld(cell);
			emitter.pos(position.x + x, position.y + y, width, height);
			emitter.shift(shiftX, shiftY);
			emitter.trackVisibility(() -> Dungeon.level != null
					&& cell >= 0
					&& cell < Dungeon.level.heroFOV.length
					&& Dungeon.level.heroFOV[cell]);
			return true;
		}

		if (TYPE_WORLD.equals(type)) {
			emitter.pos(x, y, width, height);
			emitter.shift(shiftX, shiftY);
			return true;
		}

		return false;
	}
}
