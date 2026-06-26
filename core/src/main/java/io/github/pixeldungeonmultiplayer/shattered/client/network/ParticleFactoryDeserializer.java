package io.github.pixeldungeonmultiplayer.shattered.client.network;

import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ChallengeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CorrosionParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PitfallParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SacrificialParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SpectralWallParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WebParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WoolParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.Log;
import com.watabou.noosa.particles.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ParticleFactoryDeserializer {

    private static final Map<String, FactoryDeserializer> factories = new HashMap<>();

    static {
        register("blast", BlastParticle.FACTORY);
        register("blood", BloodParticle.FACTORY);
        register("blood_burst", BloodParticle.BURST);
        register("challenge", ChallengeParticle.FACTORY);
        register("corrosion_missile", CorrosionParticle.MISSILE);
        register("corrosion_splash", CorrosionParticle.SPLASH);
        register("earth", EarthParticle.FACTORY);
        register("earth_small", EarthParticle.SMALL);
        register("earth_falling", EarthParticle.FALLING);
        register("elmo", ElmoParticle.FACTORY);
        register("energy", EnergyParticle.FACTORY);
        register("flame", FlameParticle.FACTORY);
        register("flow", FlowParticle.FACTORY);
        register("leaf_general", LeafParticle.GENERAL);
        register("leaf_level_specific", LeafParticle.LEVEL_SPECIFIC);
        register("leaf", object -> LeafParticle.factory(
                object.getInt("first_color"),
                object.getInt("second_color")
        ));
        register("pitfall4", PitfallParticle.FACTORY4);
        register("pitfall8", PitfallParticle.FACTORY8);
        register("poison_missile", PoisonParticle.MISSILE);
        register("poison_splash", PoisonParticle.SPLASH);
        register("purple_missile", PurpleParticle.MISSILE);
        register("purple_burst", PurpleParticle.BURST);
        register("rainbow_burst", RainbowParticle.BURST);
        register("sacrificial", SacrificialParticle.FACTORY);
        register("shadow_missile", ShadowParticle.MISSILE);
        register("shadow_curse", ShadowParticle.CURSE);
        register("shadow_up", ShadowParticle.UP);
        register("shaft", ShaftParticle.FACTORY);
        register("snow", SnowParticle.FACTORY);
        register("smoke", SmokeParticle.FACTORY);
        register("smoke_spew", SmokeParticle.SPEW);
        register("spark", SparkParticle.FACTORY);
        register("spark_static", SparkParticle.STATIC);
        register("spectral_wall", SpectralWallParticle.FACTORY);
        register("splash", object -> new Splash.SplashFactory(
                object.getInt("color"),
                (float)object.getDouble("dir"),
                (float)object.getDouble("cone")
        ));
        register("web", WebParticle.FACTORY);
        register("wind", WindParticle.FACTORY);
        register("wool", WoolParticle.FACTORY);
        register("goo", GooSprite.GooParticle.FACTORY);
        register("speck", object -> Speck.factory(
                object.getInt("type"),
                object.optBoolean("light_mode", false)
        ));

        register("magic_particle", MagicMissile.MagicParticle.FACTORY);
        register("magic_particle_attracting", MagicMissile.MagicParticle.ATTRACTING);
        register("magic_earth_particle", MagicMissile.EarthParticle.FACTORY);
        register("magic_earth_particle_burst", MagicMissile.EarthParticle.BURST);
        register("magic_earth_particle_attract", MagicMissile.EarthParticle.ATTRACT);
        register("shaman_particle_red", MagicMissile.ShamanParticle.RED);
        register("shaman_particle_blue", MagicMissile.ShamanParticle.BLUE);
        register("shaman_particle_purple", MagicMissile.ShamanParticle.PURPLE);
        register("white_particle", MagicMissile.WhiteParticle.FACTORY);
        register("white_particle_yellow", MagicMissile.WhiteParticle.YELLOW);
        register("white_particle_wall", MagicMissile.WhiteParticle.WALL);
        register("slow_particle", MagicMissile.SlowParticle.FACTORY);
        register("force_particle", MagicMissile.ForceParticle.FACTORY);
        register("ward_particle", MagicMissile.WardParticle.FACTORY);
        register("ward_particle_up", MagicMissile.WardParticle.UP);

        register("sewer_sink", SewerLevel.Sink.factory);
        register("pylon_energy_directed_sparks", CavesBossLevel.PylonEnergy.DIRECTED_SPARKS);
    }

    public static void register(String name, Emitter.Factory factory) {
        register(name, object -> factory);
    }

    public static void register(String name, FactoryDeserializer deserializer) {
        factories.put(name.toLowerCase(Locale.ENGLISH), deserializer);
    }

    public static Emitter.Factory deserialize(JSONObject object) throws JSONException {
        String name = JsonStringHelper.getString(object, "factory_type").toLowerCase(Locale.ENGLISH);
        FactoryDeserializer deserializer = factories.get(name);
        if (deserializer == null) {
            Log.w(ParticleFactoryDeserializer.class.getSimpleName(), String.format("No such particle factory: %s", name));
        }
        return deserializer == null ? null : deserializer.deserialize(object);
    }

    public interface FactoryDeserializer {
        Emitter.Factory deserialize(JSONObject object) throws JSONException;
    }
}
