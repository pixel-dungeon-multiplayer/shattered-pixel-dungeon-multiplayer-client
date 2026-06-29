package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.plants;

import com.shatteredpixel.shatteredpixeldungeon.plants.CustomPlant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.ActionParser;
import org.json.JSONException;
import org.json.JSONObject;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;

public class PlantUpdateParser implements ActionParser {
    @Override
    public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
        if (action.isNull("plant_info")) {
            if (level == null || level.plants == null) {
                return;
            }
            Plant plant = level.plants.get(action.getInt("pos"));
            if (plant != null) {
                plant.wither();
            }
            return;
        }
        JSONObject plantInfo = action.optJSONObject("plant_info");
        Plant.Seed seed = new CustomPlant.Seed(plantInfo);
        level.plant(seed, action.getInt("pos"));
    }
}
