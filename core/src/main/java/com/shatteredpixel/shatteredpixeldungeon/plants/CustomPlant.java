package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Reflection;

import org.json.JSONObject;

public class CustomPlant extends Plant {
    String desc;
    String plantName;

    public CustomPlant(int imageID, int pos, String name, String desc) {
        image = imageID;
        this.pos = pos;
        plantName = name;
        this.desc = desc;
    }

    @Override
    public String name(){
        return plantName;
    }
    @Override
    public String desc() {
        return desc;
    }

    public static class Seed extends Plant.Seed {
        private final JSONObject plantInfo;
        public Seed(JSONObject plantInfo){
            this.plantInfo = plantInfo;
        }
        public Plant couch( int pos, Level level ) {
            if (level != null && level.heroFOV != null && level.heroFOV[pos]) {
                Sample.INSTANCE.play(Assets.Sounds.PLANT);
            }
            Plant plant = new CustomPlant(
                    plantInfo.optInt("sprite_id"),
                    pos,
                    JsonStringHelper.optString(plantInfo, "name", "unknown"),
                    JsonStringHelper.optString(plantInfo, "desc", "unknown")
            );
            plant.pos = pos;
            return plant;
        }

    }
}
