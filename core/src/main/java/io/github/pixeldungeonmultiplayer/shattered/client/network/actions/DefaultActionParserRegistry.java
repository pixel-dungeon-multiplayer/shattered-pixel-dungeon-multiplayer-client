package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import io.github.pixeldungeonmultiplayer.shattered.client.network.JsonStringHelper;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.actors.ActorRemoveParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.actors.BlobUpdateParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.actors.CharUpdateParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.buff.BuffRemoveParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.buff.BuffUpdateParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.charsprites.CharEmoParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.charsprites.CharSpriteStateParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.charsprites.SpriteFlashParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.customtilemap.CustomTilemapAddParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.customtilemap.CustomTilemapRemoveParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.customtilemap.CustomTilemapSpecialParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.customtilemap.CustomTilemapUpdateParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.effects.CheckedCellVisualParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.effects.EnchantingVisualParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.effects.FlareVisualParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters.EmitterBurstParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters.EmitterPourParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters.EmitterStartParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.emitters.EmitterStopParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.heaps.HeapRemoveParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.heaps.HeapUpdateParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items.inventory.InventoryDefineSpecialSlotsParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items.inventory.InventoryRebuildParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items.ItemAddParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items.ItemRemoveParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items.ItemReplaceParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items.ItemUpdateParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.level.*;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.plants.PlantRemoveParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.plants.PlantUpdateParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.traps.TrapRemoveParser;
import io.github.pixeldungeonmultiplayer.shattered.client.network.actions.traps.TrapUpdateParser;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DefaultActionParserRegistry {

    public static ActionParserRegistry create() {
        ActionParserRegistry registry = new ActionParserRegistry();
        register(registry, "sprite_action", 1, new SpriteActionParser());
        register(registry, "sprite_flash", 1, new SpriteFlashParser());
        register(registry, "show_status", 1, new ShowStatusParser());
        register(registry, "wound_visual", 1, new WoundVisualParser());
        register(registry, "ripple_visual", 1, new RippleVisualParser());
        register(registry, "missile_sprite_visual", 1, new MissileSpriteVisualParser());
        register(registry, "checked_cell_visual", 1, new CheckedCellVisualParser());
        register(registry, "play_sample", 1, new PlaySampleParser());
        register(registry, "music_play", 1, new MusicParser());
        register(registry, "music_play_tracks", 1, new MusicParser());
        register(registry, "music_end", 1, new MusicParser());
        register(registry, "music_fade_out", 1, new MusicParser());
        register(registry, "load_sample", 1, new LoadSampleParser());
        register(registry, "unload_sample", 1, new UnloadSampleParser());
        register(registry, "shake_camera", 1, new ShakeCameraParser());
        register(registry, "enchanting_visual", 1, new EnchantingVisualParser());
        register(registry, "flare_visual", 1, new FlareVisualParser());
        register(registry, "emitter_burst", 1, new EmitterBurstParser());
        register(registry, "emitter_start", 1, new EmitterStartParser());
        register(registry, "emitter_pour", 1, new EmitterPourParser());
        register(registry, "emitter_stop", 1, new EmitterStopParser());
        register(registry, "heap_drop_visual", 1, new HeapDropVisualParser());
        register(registry, "magic_missile_visual", 1, new MagicMissileVisualParser());
        register(registry, "spell_sprite", 1, new SpellSpriteParser());
        register(registry, "discover_tile", 1, new DiscoverTileParser());
        register(registry, "surprise_visual", 1, new SurpriseVisualParser());
        register(registry, "boss_health_bar", 1, new BossHealthBarParser());
        register(registry, "game_scene_flash", 1, new GameSceneFlashParser());
        register(registry, "custom_tilemap_add", 1, new CustomTilemapAddParser());
        register(registry, "custom_tilemap_remove", 1, new CustomTilemapRemoveParser());
        register(registry, "update_custom_tilemap", 1, new CustomTilemapUpdateParser());
        register(registry, "custom_tilemap_special", 1, new CustomTilemapSpecialParser());
        register(registry, "show_banner", 1, new ShowBannerParser());
        register(registry, "redirect_server", 1, new RedirectServerParser());
        register(registry, "resize_level", 1, new ResizeLevelParser());
        register(registry, "set_level_visuals", 1, new SetLevelVisualsParser());
        register(registry, "set_level_entrance", 1, new SetLevelEntranceParser());
        register(registry, "set_level_exit", 1, new SetLevelExitParser());
        register(registry, "set_level_tiles", 1, new SetLevelTilesParser());
        register(registry, "set_level_states", 1, new SetLevelStatesParser());
        register(registry, "update_fov", 1, new UpdateFovParser());
        register(registry, "update_cells", 1, new UpdateCellsParser());
        register(registry, "interlevel_scene", 1, new InterlevelSceneParser());
        register(registry, "char_update", 1, new CharUpdateParser());
        register(registry, "blob_update", 1, new BlobUpdateParser());
        register(registry, "actor_remove", 1, new ActorRemoveParser());
        register(registry, "char_sprite_state_add", 1, new CharSpriteStateParser(false));
        register(registry, "char_sprite_state_remove", 1, new CharSpriteStateParser(true));
        register(registry, "char_emo", 1, new CharEmoParser());
        register(registry, "buff_update", 1, new BuffUpdateParser());
        register(registry, "buff_remove", 1, new BuffRemoveParser());
        register(registry, "hero_patch", 1, new HeroPatchParser());
        register(registry, "change_hero_actor_id", 1, new ChangeHeroActorIdParser());
        register(registry, "hero_ready", 1, new HeroReadyParser());
        register(registry, "hero_gold", 1, new HeroGoldParser());
        register(registry, "hero_uuid", 1, new HeroUUIDParser());
        register(registry, "messages", 1, new MessagesParser());
        register(registry, "inventory_rebuild", 1, new InventoryRebuildParser());
        register(registry, "inventory_define_special_slots", 1, new InventoryDefineSpecialSlotsParser());
        register(registry, "item_add", 1, new ItemAddParser());
        register(registry, "item_remove", 1, new ItemRemoveParser());
        register(registry, "item_update", 1, new ItemUpdateParser());
        register(registry, "item_replace", 1, new ItemReplaceParser());
        register(registry, "heap_update", 1, new HeapUpdateParser());
        register(registry, "heap_remove", 1, new HeapRemoveParser());
        register(registry, "update_window", 1, new UpdateWindowParser());
        register(registry, "hide_window", 1, new HideWindowParser());
        register(registry, "update_floor_info", 1, new UpdateFloorInfoParser());
        register(registry, "locked_floor_state", 1, new LockedFloorStateParser());
        register(registry, "update_counter", 1, new UpdateCounterParser());
        register(registry, "keys_indicator", 1, new KeysIndicatorParser());
        register(registry, "cell_listener_prompt", 1, new CellListenerPromptParser());
        register(registry, "attack_indicator_target", 1, new AttackIndicatorTargetParser());
        register(registry, "resume_button_visible", 1, new ResumeButtonVisibleParser());
        register(registry, "plant_update", 1, new PlantUpdateParser());
        register(registry, "plant_remove", 1, new PlantRemoveParser());
        register(registry, "trap_update", 1, new TrapUpdateParser());
        register(registry, "trap_remove", 1, new TrapRemoveParser());
        register(registry, "journal_snapshot", 1, new JournalSnapshotParser());
        register(registry, "texturepack", 1, new TexturePackParser());
        return registry;
    }

    public static ActionParser register(ActionParserRegistry registry, String actionName, int protocolVersion, ActionParser parser) {
        ActionParser previous = registry.register(actionName, protocolVersion, parser);
        if (previous != null) {
            GLog.w(
                    "Duplicate parser registration for action %s protocol %d: %s replaced by %s",
                    actionName,
                    protocolVersion,
                    previous.getClass().getSimpleName(),
                    parser.getClass().getSimpleName()
            );
        }
        return previous;
    }

    private static class SpriteActionParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            parseThread.parseSpriteAction(action);
        }
    }

    private static class ShowStatusParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            parseThread.parseShowStatusAction(action);
        }
    }

    private static class WoundVisualParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) {
            parseThread.parseWoundVisualAction(action);
        }
    }

    private static class RippleVisualParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) {
            parseThread.parseRippleVisualAction(action);
        }
    }



    private static class PlaySampleParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) {
            Sample.INSTANCE.play(action);
        }
    }

    private static class MusicParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) {
            Music.INSTANCE.parseAction(action).execute();
        }
    }

    private static class LoadSampleParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            Sample.INSTANCE.load(action.getJSONArray("samples"));
        }
    }

    private static class UnloadSampleParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            Sample.INSTANCE.unload(JsonStringHelper.getString(action, "sample"));
        }
    }

    private static class ShakeCameraParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            Camera.main.shake((float) action.getDouble("magnitude"), (float) action.getDouble("duration"));
        }
    }

    private static class HeapDropVisualParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            parseThread.parseHeadDropVisualAction(action);
        }
    }

    private static class SpellSpriteParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            parseThread.ShowSpellSprite(action);
        }
    }

    private static class DiscoverTileParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            GameScene.discoverTile(action.getInt("pos"), action.getInt("old_tile"));
        }
    }

    private static class SurpriseVisualParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            Surprise.hit(action.getInt("pos"), action.getInt("angle"));
        }
    }

    private static class BossHealthBarParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) {
            BossHealthBar.parseAction(action);
        }
    }

    private static class GameSceneFlashParser implements ActionParser {
        public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
            GameScene.flash(action.getInt("color"), action.getBoolean("light"));
        }
    }

    public static JSONObject payloadObject(JSONObject action) throws JSONException {
        return action.has("payload") ? action.getJSONObject("payload") : action;
    }

    public static JSONArray payloadArray(JSONObject action) throws JSONException {
        return action.getJSONArray("payload");
    }

}
